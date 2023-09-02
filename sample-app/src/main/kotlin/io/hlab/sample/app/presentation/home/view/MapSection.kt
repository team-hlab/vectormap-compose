package io.hlab.sample.app.presentation.home.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.hlab.vectormap.compose.KakaoMap
import io.hlab.vectormap.compose.settings.MapDirection
import io.hlab.vectormap.compose.settings.MapViewSettings
import io.hlab.vectormap.compose.settings.MapWidgetPosition

@Composable
fun MapSection(
    modifier: Modifier = Modifier,
    toggleBars: () -> Unit = {},
) {
    val density = LocalDensity.current

    val mapViewSettings = remember {
        MapViewSettings(
            compassPosition = MapWidgetPosition(
                mapDirection = MapDirection.TopRight,
                xPx = with(density) { 8.dp.toPx() },
                yPx = with(density) { 100.dp.toPx() },
            ),
        )
    }
    KakaoMap(
        modifier = modifier,
        mapViewSettings = mapViewSettings,
        onMapClick = { _ ->
            toggleBars()
        },
    )
}