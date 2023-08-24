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
import com.kakao.vectormap.MapView
import io.hlab.vectormap.compose.extension.disposingComposition
import io.hlab.vectormap.compose.extension.newComposition

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
    content: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }

    AndroidView(modifier = modifier, factory = { mapView })

    // compositions
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    LaunchedEffect(Unit) {
        disposingComposition {
            mapView.newComposition(
                lifecycle = lifecycle,
                parentComposition = parentComposition,
            ) {
                currentContent?.invoke()
            }
        }
    }
}