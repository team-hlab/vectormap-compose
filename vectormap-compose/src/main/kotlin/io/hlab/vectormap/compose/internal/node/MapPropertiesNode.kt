package io.hlab.vectormap.compose.internal.node

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMap.OnMapViewInfoChangeListener
import com.kakao.vectormap.MapViewInfo
import io.hlab.vectormap.compose.internal.MapEventListeners

/**
 * 맵 객체의 설정을 관리하는 Node
 *
 * 아래와 같은 책임을 이행한다.
 * - [mapEventListeners] 를 [KakaoMap] 에 세팅
 * - 현재 맵이 위치한 화면의 [density] 와 [layoutDirection] 를 기록, 맵 패딩 설정에 이용가능하도록 한다.
 */
internal class MapPropertiesNode(
    val map: KakaoMap,
    initialMapPadding: PaddingValues,
    var mapEventListeners: MapEventListeners,
    var density: Density,
    var layoutDirection: LayoutDirection,
) : MapNode {

    init {
        setMapPadding(paddingValues = initialMapPadding)
    }

    private val onCameraMoveStartListener = KakaoMap.OnCameraMoveStartListener { _, gestureType ->
        mapEventListeners.onCameraMoveStart(gestureType)
    }

    private val onCameraMoveEndListener = KakaoMap.OnCameraMoveEndListener { _, position, gestureType ->
        mapEventListeners.onCameraMoveEnd(position, gestureType)
    }

    override fun onAttached() {
        map.setOnPaddingChangeListener { _ ->
            mapEventListeners.onMapPaddingChange()
        }
        map.setOnMapViewInfoChangeListener(
            object : OnMapViewInfoChangeListener {
                override fun onMapViewInfoChanged(mapViewInfo: MapViewInfo?) {
                    mapViewInfo?.let { mapEventListeners.onMapViewInfoChange(it) }
                }

                override fun onMapViewInfoChangeFailed() = Unit
            },
        )
        map.setOnMapClickListener { _, latLng, _, _ ->
            mapEventListeners.onMapClick(latLng)
        }
        map.setOnCompassClickListener { _, compass ->
            compass?.let { mapEventListeners.onCompassClick(it) }
        }
        map.setOnPoiClickListener { _, position, poiType, poiId ->
            mapEventListeners.onPoiClick(position, poiType, poiId)
        }
        map.setOnTerrainClickListener { _, position, _ ->
            mapEventListeners.onTerrainClick(position)
        }
        map.setOnCameraMoveStartListener(onCameraMoveStartListener)
        map.setOnCameraMoveEndListener(onCameraMoveEndListener)
    }

    override fun onRemoved() = Unit

    /**
     * Map 의 Padding 을 설정하는 함수
     */
    internal fun setMapPadding(paddingValues: PaddingValues) {
        val node = this
        with(density) {
            map.setPadding(
                paddingValues.calculateLeftPadding(node.layoutDirection).roundToPx(),
                paddingValues.calculateTopPadding().roundToPx(),
                paddingValues.calculateRightPadding(node.layoutDirection).roundToPx(),
                paddingValues.calculateBottomPadding().roundToPx(),
            )
        }
    }
}