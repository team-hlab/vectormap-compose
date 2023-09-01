package io.hlab.sample.app.presentation.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColors(
    val naviColor: Color,
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        naviColor = Color.Unspecified,
    )
}

object SampleTheme {
    val colors: Colors
        @Composable
        get() = MaterialTheme.colors

    val customColors: CustomColors
        @Composable
        get() = LocalCustomColors.current
}

@Composable
fun SampleTheme(
    content: @Composable () -> Unit,
) {
    val customColors = CustomColors(
        naviColor = RawColors.SkyBlue,
    )

    CompositionLocalProvider(
        LocalCustomColors provides customColors,
    ) {
        MaterialTheme(
            colors = MaterialTheme.colors,
            content = content,
        )
    }
}