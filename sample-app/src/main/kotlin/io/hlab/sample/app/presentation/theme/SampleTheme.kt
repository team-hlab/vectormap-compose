package io.hlab.sample.app.presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun SampleTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider {
        MaterialTheme(
            colors = MaterialTheme.colors,
            content = content,
        )
    }
}