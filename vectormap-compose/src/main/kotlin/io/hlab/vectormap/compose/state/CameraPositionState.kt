package io.hlab.vectormap.compose.state

import androidx.annotation.UiThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import io.hlab.vectormap.compose.internal.CameraPositionParcel
import io.hlab.vectormap.compose.internal.parcelize
import io.hlab.vectormap.compose.internal.restore
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * [CameraPositionState] 를 초기설정과 함께 [rememberSaveable] 로 제공함.
 * [initialPosition] 과 [init] 을 통해 초기 설정과 함께 생성할 수 있게 만든다.
 *
 * @param key 구분을 위한 key value
 * @param initialPosition 카메라의 초기 시작 위치 정보
 * @param init 객체 생성과 함께 수행되어야 할 초기화 블록
 */
@Composable
public inline fun rememberCameraPositionState(
    key: String? = null,
    initialPosition: CameraPosition = CameraPositionDefaults.DefaultCameraPosition,
    crossinline init: CameraPositionState.() -> Unit = {},
): CameraPositionState = rememberSaveable(key = key, saver = CameraPositionState.Saver) {
    CameraPositionState(initialPosition = initialPosition)
        .apply(init)
}

/**
 * 카메라 위치정보를 들고 있는 상태 객체.
 *
 * 해당 객체는 아래와 같은 역할을 수행한다.
 * - 내부적으로 카메라 관련된 상태의 관리
 * - 현재 지도의 카메라 정보에 대한 획득
 * - 카메라의 이동 수행과 같은 액션.
 */
public class CameraPositionState(
    initialPosition: CameraPosition = CameraPositionDefaults.DefaultCameraPosition,
) {
    // SideEffect 를 thread-safe 하게 실행할 수 있도록 Lock 을 활용한다.
    // mutable-value 들을 보호해 값의 제어를 원활하게 한다.
    private val lock = Unit

    // 현재 CameraPositionState 가 붙어있는 KakaoMap 객체
    private var map: KakaoMap? by mutableStateOf(null)

    /**
     * Capture 가능한 최신의 카메라 포지션.
     * - `map` 이 있다면 : [position] setter 로 카메라를 이동
     * - `map` 이 없다면 : [position] 이 마지막으로 설정된 값
     */
    internal var rawPosition by mutableStateOf(initialPosition)

    internal val lastSaveablePosition get() = rawPosition

    /**
     * 카메라가 현재 이동중 인지를 나타냄.
     */
    public var isMoving: Boolean by mutableStateOf(false)
        internal set

    // A token representing the current owner of any ongoing motion in progress.
    // Used to determine if map animation should stop when calls to animate end.
    // Guarded by `lock`.
    private var movementOwner: Any? by mutableStateOf(null)

    /**
     * 현재 카메라가 위치해 있는 포지션 정보.
     *
     * Attach 된 [KakaoMap] 이 존재하지 않는 경우, 마지막으로 API 를 통해 set 된 값을 들고 있음.
     */
    public var position: CameraPosition
        get() = rawPosition
        set(value) {
            synchronized(lock) {
                val map = map
                if (map == null) {
                    rawPosition = value
                } else {
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(value))
                }
            }
        }

    /**
     * 지도가 최대로 축소될 수 있는 값.
     */
    public val minZoomLevel: Int
        get() = map?.minZoomLevel ?: CameraPositionDefaults.MinZoomLevel

    /**
     * 지도가 최대로 확대될 수 있는 값.
     */
    public val maxZoomLevel: Int
        get() = map?.maxZoomLevel ?: CameraPositionDefaults.MaxZoomLevel

    /**
     * 현재 카메라의 확대/축소 수준의 값.
     */
    public val zoomLevel: Int?
        get() = map?.zoomLevel

    // An action to run when the map becomes available or unavailable.
    // represents a mutually exclusive mutation to perform while holding `lock`.
    // Guarded by `lock`.
    private var onMapChanged: OnMapChangedCallback? by mutableStateOf(null)

    /**
     * Set [onMapChanged] to [callback], invoking the current callback's
     * [OnMapChangedCallback.onCancelLocked] if one is present.
     */
    private fun doOnMapChangedLocked(callback: OnMapChangedCallback) {
        onMapChanged?.onCancelLocked()
        onMapChanged = callback
    }

    /**
     * Used with [onMapChangedLocked] to execute one-time actions when a map becomes available
     * or is made unavailable. Cancellation is provided in order to resume suspended coroutines
     * that are awaiting the execution of one of these callbacks that will never come.
     */
    private fun interface OnMapChangedCallback {
        fun onMapChangedLocked(newMap: KakaoMap?)
        fun onCancelLocked() {}
    }

    // The current map is set and cleared by side effect.
    // There can be only one associated at a time.
    internal fun setMap(map: KakaoMap?) {
        synchronized(lock) {
            if (this.map == null && map == null) return
            if (this.map != null && map != null) {
                error("CameraPositionState may only be associated with one KakaoMap at a time")
            }
            this.map = map
            if (map == null) {
                isMoving = false
            } else {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(position))
            }
            onMapChanged?.let {
                // Clear this first since the callback itself might set it again for later
                onMapChanged = null
                it.onMapChangedLocked(map)
            }
        }
    }

    /**
     * 즉각적인 카메라 이동을 수행합니다.
     *
     * @param update 수행할 CameraUpdate 정보
     */
    @UiThread
    public fun move(update: CameraUpdate) {
        synchronized(lock) {
            val map = map
            movementOwner = null
            if (map == null) {
                doOnMapChangedLocked { it?.moveCamera(update) }
            } else {
                map.moveCamera(update)
            }
        }
    }

    /**
     * 애니메이션이 적용된 카메라 이동을 수행합니다.
     * 카메라가 해당 API 를 이용해 이동중일 경우, [position] 은 카메라 위치를 반영합니다.
     *
     * 현 [CameraPositionState] 가 [KakaoMap] 에 아직 바인딩 되기 전이라면, 바인딩 된 후 곧바로 마지막 이동을 수행합니다.
     *
     * 아래의 경우에 의해 [move] 가 중단되면, [CancellationException] 이 발생합니다.
     * - [KakaoMap] 이 변경되는 경우 ( Composable 이 아예 재시작되거나, 다른 Composable 에 대입하는 경우 )
     * - [position] 이 `state.position = CameraPosition(...)` 과 같이 직접 재정의 되어 override 되는 경우
     * - [move] 가 수행중인 경우, 새로운 [move] 가 호출된 경우
     * - [move] 를 호출하는 [Job] 이 취소된 경우
     *
     * @param update 수행할 CameraUpdate 정보
     * @param animation 수행할 CameraAnimation 정보
     */
    @UiThread
    public suspend fun move(
        update: CameraUpdate,
        animation: CameraAnimation,
    ) {
        val myJob = currentCoroutineContext()[Job]
        try {
            suspendCancellableCoroutine<Unit> { continuation ->
                synchronized(lock) {
                    movementOwner = myJob
                    val map = map // value-capturing
                    if (map == null) {
                        val animateOnMapAvailable = object : OnMapChangedCallback {
                            override fun onMapChangedLocked(newMap: KakaoMap?) {
                                if (newMap == null) {
                                    continuation.resumeWithException(CancellationException("No KakaoMap available !!"))
                                    error("No KakaoMap available to launch animation")
                                }
                                performMoveCameraLocked(newMap, update, animation, continuation)
                            }

                            override fun onCancelLocked() {
                                continuation.resumeWithException(CancellationException("Animation Canceled"))
                            }
                        }
                        doOnMapChangedLocked(animateOnMapAvailable)
                        continuation.invokeOnCancellation {
                            synchronized(lock) {
                                if (onMapChanged === animateOnMapAvailable) {
                                    // 외부에서 발생한 Cancellation 은 onCancel 블록을 실행하지 못함.
                                    // 이에 null 을 명시적으로 onMapChanged 에 할당해, 수행을 방지함.
                                    onMapChanged = null
                                }
                            }
                        }
                    } else {
                        performMoveCameraLocked(map, update, animation, continuation)
                    }
                }
            }
        } finally {
            // continuation.invokeOnCancellation 이 발생한 경우 ( 다른 animation 요청이 들어온 경우 )
            // 수행중이던 animation 을 멈추고 다른 요청에 dispatcher 사용을 보장한다.
            synchronized(lock) {
                if (myJob != null && movementOwner === myJob) {
                    movementOwner = null
                }
            }
        }
    }

    private fun performMoveCameraLocked(
        map: KakaoMap,
        update: CameraUpdate,
        animation: CameraAnimation,
        continuation: CancellableContinuation<Unit>,
    ) {
        try {
            map.moveCamera(update, animation)
            continuation.resume(Unit)
        } catch (e: Exception) {
            continuation.resumeWithException(CancellationException(""))
        }
        doOnMapChangedLocked {
            check(it == null) {
                "New KakaoMap unexpectedly set while an animation was still running"
            }
        }
    }

    public companion object {
        /**
         * The default saver implementation for [CameraPositionState]
         */
        public val Saver: Saver<CameraPositionState, CameraPositionParcel> = Saver(
            save = { it.lastSaveablePosition.parcelize() },
            restore = { CameraPositionState(it.restore()) },
        )
    }
}