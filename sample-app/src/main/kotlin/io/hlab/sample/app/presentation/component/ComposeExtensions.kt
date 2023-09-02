package io.hlab.sample.app.presentation.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun HSpacer(width: Dp) = Spacer(modifier = Modifier.width(width))

@Composable
fun VSpacer(height: Dp) = Spacer(modifier = Modifier.height(height))