package io.hlab.vectormap.compose.internal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import io.hlab.vectormap.compose.extension.getMap
import io.hlab.vectormap.compose.internal.node.MapPropertiesNode

@Suppress("NOTHING_TO_INLINE")
@Composable
internal inline fun MapUpdater(
    mapEventListeners: MapEventListeners,
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
            // EventListener 변화 감지
            update(mapEventListeners) { this.mapEventListeners = it }
        },
    )
}