package io.hlab.vectormap.compose.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import com.kakao.vectormap.KakaoMap
import io.hlab.vectormap.compose.internal.MapApplier

internal val NoPadding = PaddingValues()

@Composable
internal fun getMap(): KakaoMap = (currentComposer.applier as MapApplier).map