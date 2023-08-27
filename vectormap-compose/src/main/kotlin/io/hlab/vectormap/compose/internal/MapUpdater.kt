package io.hlab.vectormap.compose.internal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.MapType
import com.kakao.vectormap.MapView
import io.hlab.vectormap.compose.extension.getMap
import io.hlab.vectormap.compose.internal.node.MapPropertiesNode
import io.hlab.vectormap.compose.settings.MapViewSettings
import io.hlab.vectormap.compose.settings.adjustDimScreenType

/**
 * MapProperty 를 최신 상태로 유지하기 위한 Composable.
 *
 * [MapUpdater] 는 [MapView] 의 Composition 범위 내에 항상 속해있으며, 컴포지션에 관여해 상태를 업데이트 할 수 있도록 한다.
 */
@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MapUpdater(
    mapEventListeners: MapEventListeners,
    mapViewSettings: MapViewSettings,
    mapPadding: PaddingValues,
) {
    // 지도 객체 및 화면 관련 정보
    val map = getMap()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    // 주입받는 State 들에 대해 ComposeNode 를 recompose 해 상태를 갱신함.
    // 매번 LayoutNode 에 대한 직접적인 recompose 가 매번 일어나는 걸 방지할 수 있음.
    ComposeNode<MapPropertiesNode, MapApplier>(
        factory = {
            MapPropertiesNode(
                map = map,
                initialMapPadding = mapPadding,
                mapEventListeners = mapEventListeners,
                density = density,
                layoutDirection = layoutDirection,
            )
        },
        update = {
            // 노드에 density 와 layoutDirection 을 가지고 있도록 함으로써
            // 업데이트 블록이 캡쳐링 되지 않고, 컴파일러가 해당 블록을 싱글톤으로 변경할 수 있도록 한다.
            update(density) { this.density = it }
            update(layoutDirection) { this.layoutDirection = it }
            update(mapPadding) { this.setMapPadding(it) }
            // Map View Setting 설정
            update(mapViewSettings.baseMap) { mapType -> map.adjustBaseMap(mapType) }
            set(mapViewSettings.isCameraAnimateEnabled) { map.setCameraAnimateEnable(it) }
            set(mapViewSettings.isTrackingRotation) { map.trackingManager?.setTrackingRotation(it) }
            set(mapViewSettings.logoPosition) { position ->
                position?.let { map.logo?.setPosition(it.mapDirection.value, it.xPx, it.yPx) }
            }
            set(mapViewSettings.compassPosition) { position ->
                position?.let { map.compass?.setPosition(it.mapDirection.value, it.xPx, it.yPx) }
            }
            set(mapViewSettings.isCompassVisible) { compassVisibility ->
                if (compassVisibility) map.compass?.show() else map.compass?.hide()
            }
            set(mapViewSettings.isCompassBackToNorthOnClick) { map.compass?.isBackToNorthOnClick = it }
            set(mapViewSettings.scaleBarPosition) { position ->
                position?.let { map.scaleBar?.setPosition(it.mapDirection.value, it.xPx, it.yPx) }
            }
            set(mapViewSettings.isScaleBarVisible) { scaleBarVisibility ->
                if (scaleBarVisibility) map.scaleBar?.show() else map.scaleBar?.hide()
            }
            set(mapViewSettings.isScaleBarAutoHide) { map.scaleBar?.isAutoHide = it }
            set(mapViewSettings.isPoiVisible) { map.setPoiVisible(it) }
            set(mapViewSettings.isPoiClickable) { map.setPoiClickable(it) }
            set(mapViewSettings.poiLanguage) { map.setPoiLanguage(it.code) }
            set(mapViewSettings.poiScale) { map.setPoiScale(it) }
            set(mapViewSettings.buildingHeightScale) { map.buildingHeightScale = it }
            set(mapViewSettings.fixedCenterGestures) { fixedCenterGestures ->
                val nonFixedGestures = (gestureTypes - fixedCenterGestures).toTypedArray()
                if (nonFixedGestures.isNotEmpty()) map.setFixedCenter(false, *nonFixedGestures)
                if (fixedCenterGestures.isNotEmpty()) map.setFixedCenter(true, *fixedCenterGestures.toTypedArray())
            }
            set(mapViewSettings.dimScreenType) { map.adjustDimScreenType(it) }
            set(mapViewSettings.dimScreenColor) { color ->
                val dimScreenLayer = map.dimScreenManager?.dimScreenLayer ?: return@set
                dimScreenLayer.setColor(color.toArgb())
            }
            // EventListener 변화 감지
            update(mapEventListeners) { this.mapEventListeners = it }
        },
    )
}

private val gestureTypes = GestureType.values().toSet()

/**
 * 기본 맵 스타일을 지정합니다.
 *
 * 현재 아래와 같은 맵 스타일이 지원됩니다.
 * - [MapType.NORMAL] : 기본 맵뷰
 * - [MapType.SKYVIEW] : 스카이맵 맵뷰
 */
private fun KakaoMap.adjustBaseMap(mapType: MapType) = changeMapType(mapType)