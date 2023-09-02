package io.hlab.sample.app.presentation.home.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.hlab.sample.app.R
import io.hlab.sample.app.presentation.component.HSpacer
import io.hlab.sample.app.presentation.component.VSpacer
import io.hlab.sample.app.presentation.home.HomeTab
import io.hlab.sample.app.presentation.theme.SampleTheme

@Composable
fun MainMenuBarSection(
    query: String,
    isShowing: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(
        targetState = isShowing,
        label = "barTransition",
    )
    Box(modifier = modifier) {
        // TopBar
        transition
            .AnimatedContent(
                modifier = Modifier.align(Alignment.TopCenter),
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
                    ) togetherWith slideOutVertically(
                        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
                    ) { height ->
                        -height
                    } using SizeTransform(clip = false)
                },
            ) { showing ->
                if (showing) {
                    TopBar(query = query)
                }
            }
        // BottomBar
        transition
            .AnimatedContent(
                modifier = Modifier.align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
                    ) { height ->
                        height
                    } togetherWith slideOutVertically(
                        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
                    ) { height ->
                        height
                    } using SizeTransform(clip = false)
                },
            ) { showing ->
                if (showing) {
                    BottomBar()
                }
            }
    }
}

@Composable
private fun TopBar(
    query: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .minimumInteractiveComponentSize()
            .height(IntrinsicSize.Max)
            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
            .background(color = SampleTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        SearchingBar(
            query = query,
            modifier = Modifier.weight(weight = 1f, fill = true),
        )
        HSpacer(width = 8.dp)
        NavigationButton()
    }
}

@Composable
private fun SearchingBar(
    query: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_h32_menu),
                tint = SampleTheme.colors.onSurface,
                contentDescription = "searchingBar_menu",
            )
            HSpacer(width = 8.dp)
            Text(
                modifier = Modifier.weight(weight = 1f),
                text = query,
                color = SampleTheme.colors.onSurface.copy(alpha = 0.4f),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            HSpacer(width = 8.dp)
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_h32_voice),
                tint = SampleTheme.colors.onSurface,
                contentDescription = "searchingBar_menu",
            )
        }
    }
}

@Composable
private fun NavigationButton(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .size(48.dp)
            .clickable { onNavigationClick() },
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .background(color = SampleTheme.customColors.naviColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.ic_h32_navigation),
                contentDescription = "navigation",
                tint = SampleTheme.colors.surface,
            )
            VSpacer(height = 2.dp)
            Text(
                text = "길찾기",
                color = SampleTheme.colors.surface,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = Modifier
            .background(color = SampleTheme.colors.surface)
            .fillMaxWidth(),
        elevation = 8.dp,
    ) {
        Row(
            modifier = modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .minimumInteractiveComponentSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeTab.entries.forEach { tab ->
                BottomTabRow(
                    tab = tab,
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomTabRow(
    tab: HomeTab,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .weight(weight = 1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = tab.titleResId),
        )
    }
}