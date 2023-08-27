package io.hlab.vectormap.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import com.kakao.vectormap.Compass
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapView
import com.kakao.vectormap.MapViewInfo
import com.kakao.vectormap.camera.CameraPosition
import io.hlab.vectormap.compose.extension.NoPadding
import io.hlab.vectormap.compose.extension.disposingComposition
import io.hlab.vectormap.compose.extension.newComposition
import io.hlab.vectormap.compose.internal.MapEventListeners
import io.hlab.vectormap.compose.internal.MapLifecycleCallbacks
import io.hlab.vectormap.compose.internal.MapUpdater
import io.hlab.vectormap.compose.settings.MapInitialOptions

/**
 * [com.kakao.vectormap.KakaoMap] 을 제공하는 컴포저블
 *
 * 주어진 파라미터를 기반으로 지도 뷰를 그려주는 Compose View.
 *
 * @param modifier 지도가 그려질 영역에 대한 [Modifier]
 * @param content 다양한 컴포저블을 이용해 지도 뷰를 풍부하게 사용할 수 있음.
 */
@Composable
fun KakaoMap(
    modifier: Modifier = Modifier,
    mapInitialOptions: MapInitialOptions = MapInitialOptions(),
    contentDescription: String? = null,
    mapPadding: PaddingValues = NoPadding,
    onMapReady: (KakaoMap) -> Unit = {},
    onMapResumed: () -> Unit = {},
    onMapPaused: () -> Unit = {},
    onMapError: (Exception) -> Unit = {},
    onMapDestroy: () -> Unit = {},
    onMapPaddingChange: () -> Unit = {},
    onMapViewInfoChange: (MapViewInfo) -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    onCompassClick: (Compass) -> Unit = {},
    onPoiClick: (LatLng, String, String) -> Unit = { _, _, _ -> },
    onTerrainClick: (LatLng) -> Unit = { _ -> },
    onCameraMoveStart: (GestureType) -> Unit = {},
    onCameraMoveEnd: (CameraPosition, GestureType) -> Unit = { _, _ -> },
    content: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }

    val semantics = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
        }
    } else {
        Modifier
    }

    AndroidView(modifier = modifier.then(semantics), factory = { mapView })

    // callback Containers
    // remember 를 이용해 sub-composition 을 구독해 content invoke 가 매번 일어나지 않도록 컨트롤함
    // 이와 같이 구성하면, 새로운 content invoke 를 제공하지 않으면서 sub-composition 에 대한 갱신이 가능함
    val mapLifecycleCallbacks = remember { MapLifecycleCallbacks() }.also {
        it.onMapReady = onMapReady
        it.onMapResumed = onMapResumed
        it.onMapPaused = onMapPaused
        it.onMapError = onMapError
        it.onMapDestroy = onMapDestroy
    }
    val mapEventListeners = remember { MapEventListeners() }.also {
        it.onMapPaddingChange = onMapPaddingChange
        it.onMapViewInfoChange = onMapViewInfoChange
        it.onMapClick = onMapClick
        it.onCompassClick = onCompassClick
        it.onPoiClick = onPoiClick
        it.onTerrainClick = onTerrainClick
        it.onCameraMoveStart = onCameraMoveStart
        it.onCameraMoveEnd = onCameraMoveEnd
    }
    // map settings
    val currentMapPadding by rememberUpdatedState(mapPadding)

    // compositions
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    LaunchedEffect(Unit) {
        disposingComposition {
            mapView.newComposition(
                lifecycle = lifecycle,
                parentComposition = parentComposition,
                mapInitialOptions = mapInitialOptions,
                mapCallbackContainer = mapLifecycleCallbacks,
            ) {
                MapUpdater(
                    mapEventListeners = mapEventListeners,
                    mapPadding = currentMapPadding,
                )
                currentContent?.invoke()
            }
        }
    }
}