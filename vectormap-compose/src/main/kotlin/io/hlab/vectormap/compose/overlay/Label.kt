package io.hlab.vectormap.compose.overlay

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Updater
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ComposeView
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.TransformMethod
import com.kakao.vectormap.label.Transition
import io.hlab.vectormap.compose.annotation.KakaoMapComposable
import io.hlab.vectormap.compose.extension.getMap
import io.hlab.vectormap.compose.extension.startTrackingCompat
import io.hlab.vectormap.compose.extension.stopTrackingCompat
import io.hlab.vectormap.compose.internal.MapApplier
import io.hlab.vectormap.compose.internal.node.LabelNode
import io.hlab.vectormap.compose.internal.renderComposeViewOnce
import io.hlab.vectormap.compose.internal.toNativeBitmap
import io.hlab.vectormap.compose.state.LocalCameraPositionState
import java.util.UUID

/**
 * Label 의 설정 값에 대한 Default Value Provider
 */
public object LabelDefaults {
    public const val MinZoom: Int = 0
    public const val MoveDuration: Int = 300

    public val MapLabelTransition: LabelTransition = LabelTransition.from(Transition.Alpha, Transition.Alpha)
    public val Transform: TransformMethod = TransformMethod.Default
    public val Anchor: Offset = Offset(0.5f, 0.5f)
}

@KakaoMapComposable
@Composable
public fun Label(
    position: LatLng,
    @DrawableRes iconResId: Int? = null,
    anchor: Offset = LabelDefaults.Anchor,
    rotate: Float = 0f,
    labelId: String? = null,
    tag: String? = null,
    rank: Long = 0L,
    transition: LabelTransition = LabelDefaults.MapLabelTransition,
    transform: TransformMethod = LabelDefaults.Transform,
    enableAnimateMove: Boolean = true,
    animateMoveDuration: Int = LabelDefaults.MoveDuration,
    isVisible: Boolean = true,
    isClickable: Boolean = true,
    onClick: (Label) -> Unit = {},
    isApplyDpScale: Boolean = true,
    isTracking: Boolean = false,
    minZoom: Int = LabelDefaults.MinZoom,
    children:
    @KakaoMapComposable @Composable
    () -> Unit = {},
) {
    LabelImpl(
        position = position,
        iconResId = iconResId,
        anchor = anchor,
        rotate = rotate,
        labelId = labelId,
        transition = transition,
        tag = tag,
        rank = rank,
        transform = transform,
        enableAnimateMove = enableAnimateMove,
        animateMoveDuration = animateMoveDuration,
        isVisible = isVisible,
        isClickable = isClickable,
        onClick = onClick,
        isApplyDpScale = isApplyDpScale,
        isTracking = isTracking,
        minZoom = minZoom,
        children = children,
    )
}

@KakaoMapComposable
@Composable
public fun Label(
    position: LatLng,
    bitmap: Bitmap? = null,
    anchor: Offset = LabelDefaults.Anchor,
    rotate: Float = 0f,
    labelId: String? = null,
    tag: String? = null,
    rank: Long = 0L,
    transition: LabelTransition = LabelDefaults.MapLabelTransition,
    transform: TransformMethod = LabelDefaults.Transform,
    enableAnimateMove: Boolean = true,
    animateMoveDuration: Int = LabelDefaults.MoveDuration,
    isVisible: Boolean = true,
    isClickable: Boolean = true,
    onClick: (Label) -> Unit = {},
    isApplyDpScale: Boolean = true,
    isTracking: Boolean = false,
    minZoom: Int = LabelDefaults.MinZoom,
    children:
    @KakaoMapComposable @Composable
    () -> Unit = {},
) {
    LabelImpl(
        position = position,
        bitmap = bitmap,
        anchor = anchor,
        rotate = rotate,
        labelId = labelId,
        transition = transition,
        tag = tag,
        rank = rank,
        transform = transform,
        enableAnimateMove = enableAnimateMove,
        animateMoveDuration = animateMoveDuration,
        isVisible = isVisible,
        isClickable = isClickable,
        onClick = onClick,
        isApplyDpScale = isApplyDpScale,
        isTracking = isTracking,
        minZoom = minZoom,
        children = children,
    )
}

/**
 * [Label] Composable
 *
 * @param position 라벨이 표시될 지리적 위치 ([LatLng]) 을 지정합니다.
 * @param icon 아이콘으로 사용할 컴포즈 뷰를 지정합니다.
 * @param iconId 아이콘으로 사용되는 컴포즈 뷰의 변화를 감지할 UniqueId 를 지정합니다.
 * 컴포즈 뷰에 대한 비트맵 생성은 큰 비용을 요하므로 갱신이 필요할 때, [iconId] 값의 변화를 트리거로 사용할 수 있도록 지원합니다.
 * @param anchor 앵커를 지원한다. 앵커는 아이콘 이미지에서 기준이 되는 지점을 의미합니다. 앵커로 지정된 지점이 마커의 좌표에 위치하게 됩니다.
 * 값의 범위는 (0, 0)~(1, 1) 이며, (0, 0) 일 경우 이미지의 왼쪽 위 / (1, 1) 일 경우 이미지의 오른쪽 아래에 위치합니다.
 * 기본값은 [LabelDefaults.Anchor] 이며 정 중앙에 위치하게 됩니다.
 * @param rotate 아이콘의 각도를 지정합니다. 각도를 지정하면 아이콘이 해당 각도만큼 시계 방향으로 회전합니다. 기본값은 0입니다.
 * @param labelId 라벨의 아이디를 지정합니다. 기본값은 null 이며 이 경우, 유니크한 값으로 자동 생성됩니다.
 * @param tag
 * @param rank
 * @param transition
 * @param transform
 * @param enableAnimateMove
 * @param animateMoveDuration
 * @param isVisible
 * @param isClickable
 * @param onClick
 * @param isApplyDpScale
 * @param isTracking
 * @param minZoom
 * @param children
 */
@KakaoMapComposable
@Composable
public fun Label(
    position: LatLng,
    icon: @Composable () -> Unit,
    iconId: String = remember { UUID.randomUUID().toString() },
    anchor: Offset = LabelDefaults.Anchor,
    rotate: Float = 0f,
    labelId: String? = null,
    tag: String? = null,
    rank: Long = 0L,
    transition: LabelTransition = LabelDefaults.MapLabelTransition,
    transform: TransformMethod = LabelDefaults.Transform,
    enableAnimateMove: Boolean = true,
    animateMoveDuration: Int = LabelDefaults.MoveDuration,
    isVisible: Boolean = true,
    isClickable: Boolean = true,
    onClick: (Label) -> Unit = {},
    isApplyDpScale: Boolean = true,
    isTracking: Boolean = false,
    minZoom: Int = LabelDefaults.MinZoom,
    children:
    @KakaoMapComposable @Composable
    () -> Unit = {},
) {
    LabelImpl(
        position = position,
        icon = icon,
        iconId = iconId,
        anchor = anchor,
        rotate = rotate,
        labelId = labelId,
        transition = transition,
        tag = tag,
        rank = rank,
        transform = transform,
        enableAnimateMove = enableAnimateMove,
        animateMoveDuration = animateMoveDuration,
        isVisible = isVisible,
        isClickable = isClickable,
        onClick = onClick,
        isApplyDpScale = isApplyDpScale,
        isTracking = isTracking,
        minZoom = minZoom,
        children = children,
    )
}

/**
 * [Label] 의 [ComposeNode] 구현체
 *
 * - [LabelStyle] 및 [LabelOptions] 에 대한 실제 오버레이 적용을 담당한다.
 * - 실제 MapView 에서 관리되는 Label 에 대한 관리 및 처리를 Compose 로 위임해 처리할 수 있도록 한다.
 */
@KakaoMapComposable
@Composable
private fun LabelImpl(
    position: LatLng,
    @DrawableRes iconResId: Int? = null,
    bitmap: Bitmap? = null,
    icon: (@Composable () -> Unit)? = null,
    iconId: String? = null,
    anchor: Offset = LabelDefaults.Anchor,
    rotate: Float = 0f,
    labelId: String? = null,
    tag: String? = null,
    rank: Long = 0L,
    transition: LabelTransition = LabelDefaults.MapLabelTransition,
    transform: TransformMethod = LabelDefaults.Transform,
    enableAnimateMove: Boolean = true,
    animateMoveDuration: Int = LabelDefaults.MoveDuration,
    isVisible: Boolean = true,
    isClickable: Boolean = true,
    onClick: (Label) -> Unit = {},
    isApplyDpScale: Boolean = true,
    isTracking: Boolean = false,
    minZoom: Int = LabelDefaults.MinZoom,
    children:
    @KakaoMapComposable @Composable
    () -> Unit = {},
) {
    val applier = checkNotNull(currentComposer.applier as? MapApplier)
    val map = getMap()
    val mapView = applier.mapView
    val currentCameraPositionState = LocalCameraPositionState.current
    val compositionContext = rememberCompositionContext()
    // style 을 remember 해 다시 그려져야 하는 경우만 bitmap 등에 대해 새로 그릴 수 있도록 처리함. (퍼포먼스 개선)
    val style: LabelStyle = remember(iconResId, bitmap, iconId, isApplyDpScale, anchor, minZoom, transition) {
        val impl = when {
            iconResId != null -> LabelStyle.from(iconResId)
            bitmap != null -> LabelStyle.from(bitmap)
            icon != null -> {
                val view = ComposeView(mapView.context).apply {
                    setContent(icon)
                }
                mapView.renderComposeViewOnce(view, parentContext = compositionContext)
                LabelStyle.from(view.toNativeBitmap())
            }
            else -> error("Error while adding Label!")
        }
        impl.apply {
            setApplyDpScale(isApplyDpScale)
            anchorPoint = PointF(anchor.x, anchor.y)
            setZoomLevel(minZoom)
            iconTransition = transition
        }
    }

    // node factory function
    fun nodeFactory(): LabelNode {
        val options = if (labelId != null) {
            LabelOptions.from(labelId, position)
        } else {
            LabelOptions.from(position)
        }.apply {
            tag?.let { setTag(it) }
            setStyles(style)
            setTransform(transform)
            setRank(rank)
            setClickable(isClickable)
            setVisible(isVisible)
        }
        // Label 생성 후 property 변경 등을 용이하게 하기 위해 인스턴스를 가져온다.
        val overlay = checkNotNull(map.labelManager?.layer?.addLabel(options)) {
            "Error adding Label!"
        }

        if (isTracking) map.trackingManager?.startTracking(overlay)

        if (rotate != 0f) overlay.rotateTo(rotate)

        return LabelNode(
            overlay = overlay,
            onLabelClick = onClick,
        )
    }

    // node updater function
    fun Updater<LabelNode>.updater() {
        update(onClick) { this.onLabelClick = it }
        // set label styles
        update(style) { style ->
            val prevStyles = this.overlay.styles.getStyles()
            with(this.overlay) {
                setStyles(style)
                invalidate() // 명시적으로 style 변경을 요청을 해 변경이 이뤄지도록 한다.
            }
            prevStyles.forEach { it.iconBitmap?.recycle() }
        }
        // set label position
        update(position) { pos ->
            if (enableAnimateMove) {
                this.overlay.moveTo(pos, animateMoveDuration)
            } else {
                this.overlay.moveTo(pos)
            }
        }
        update(rotate) { this.overlay.rotateTo(it, 500) }
        update(isTracking) { enableTracking ->
            val trackingManager = map.trackingManager ?: return@update
            if (enableTracking) {
                trackingManager.startTrackingCompat(
                    label = this.overlay,
                    cameraPositionState = currentCameraPositionState,
                )
            } else {
                trackingManager.stopTrackingCompat(cameraPositionState = currentCameraPositionState)
            }
        }
        // label options control
        update(tag) { tag -> tag?.let { this.overlay.tag = it } }
        update(rank) { this.overlay.changeRank(it) }
        update(isVisible) { visible -> if (visible) this.overlay.show() else this.overlay.hide() }
        update(isClickable) { this.overlay.isClickable = it }
    }

    ComposeNode<LabelNode, MapApplier>(
        factory = ::nodeFactory,
        update = { updater() },
        content = children,
    )
}