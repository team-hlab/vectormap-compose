package io.hlab.vectormap.compose.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionContext
import androidx.lifecycle.Lifecycle
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import io.hlab.vectormap.compose.internal.LifecycleAwareKakaoMapReadyCallback
import io.hlab.vectormap.compose.internal.MapApplier
import io.hlab.vectormap.compose.internal.MapLifecycleCallbacks
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Map 에 대한 [Composition] 을 생성.
 *
 * - Lifecycle Aware 하게 [KakaoMap] 객체를 가져옴.
 * - [content] 를 명시적으로 Layout 하고, Composition 객체를 반환함.
 *
 * @return [MapApplier] 를 적용한 [MapView] 의 Composition 반환.
 */
internal suspend inline fun MapView.newComposition(
    lifecycle: Lifecycle,
    parentComposition: CompositionContext,
    mapCallbackContainer: MapLifecycleCallbacks,
    noinline content: @Composable () -> Unit,
): Composition {
    val map = awaitMap(lifecycle = lifecycle, mapCallbackContainer = mapCallbackContainer)
    return Composition(applier = MapApplier(map = map, mapView = this), parent = parentComposition)
        .apply { setContent(content) }
}

private suspend inline fun MapView.awaitMap(
    lifecycle: Lifecycle,
    mapCallbackContainer: MapLifecycleCallbacks,
): KakaoMap {
    return suspendCoroutine { continuation ->
        getMapAsync(lifecycle = lifecycle, mapCallbackContainer) { kakaoMap ->
            continuation.resume(kakaoMap)
        }
    }
}

private fun MapView.getMapAsync(
    lifecycle: Lifecycle,
    mapCallbackContainer: MapLifecycleCallbacks,
    onMapReady: (KakaoMap) -> Unit,
) {
    start(
        object : MapLifeCycleCallback() {
            override fun onMapPaused() {
                mapCallbackContainer.onMapPaused
            }

            override fun onMapResumed() {
                mapCallbackContainer.onMapResumed
            }

            override fun onMapDestroy() {
                mapCallbackContainer.onMapDestroy
            }

            override fun onMapError(error: Exception) {
                mapCallbackContainer.onMapError(error)
            }
        },

        object : LifecycleAwareKakaoMapReadyCallback(lifecycle = lifecycle) {
            override fun onLifecycleAwareMapReady(kakaoMap: KakaoMap) {
                onMapReady(kakaoMap)
                mapCallbackContainer.onMapReady(kakaoMap)
            }
        },
    )
}