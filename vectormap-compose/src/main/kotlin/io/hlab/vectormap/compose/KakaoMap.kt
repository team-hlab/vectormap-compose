package io.hlab.vectormap.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.MapView
import io.hlab.vectormap.compose.extension.disposingComposition
import io.hlab.vectormap.compose.extension.newComposition
import io.hlab.vectormap.compose.internal.MapLifecycleCallbacks

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
    onMapReady: (KakaoMap) -> Unit = {},
    onMapResumed: () -> Unit = {},
    onMapPaused: () -> Unit = {},
    onMapError: (Exception) -> Unit = {},
    onMapDestroy: () -> Unit = {},
    content: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }

    AndroidView(modifier = modifier, factory = { mapView })

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
    // compositions
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    LaunchedEffect(Unit) {
        disposingComposition {
            mapView.newComposition(
                lifecycle = lifecycle,
                parentComposition = parentComposition,
                mapCallbackContainer = mapLifecycleCallbacks,
            ) {
                currentContent?.invoke()
            }
        }
    }
}