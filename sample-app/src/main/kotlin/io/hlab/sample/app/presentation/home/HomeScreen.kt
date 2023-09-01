package io.hlab.sample.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.hlab.sample.app.R
import io.hlab.sample.app.presentation.component.HSpacer
import io.hlab.sample.app.presentation.component.VSpacer
import io.hlab.sample.app.presentation.theme.SampleTheme
import io.hlab.vectormap.compose.KakaoMap
import io.hlab.vectormap.compose.settings.MapDirection
import io.hlab.vectormap.compose.settings.MapViewSettings
import io.hlab.vectormap.compose.settings.MapWidgetPosition

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
    ) { contentPadding ->
        MainContent(
            modifier = Modifier
                .padding(contentPadding),
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        MainKakaoMap(
            modifier = Modifier.fillMaxSize(),
        )
        TopBar(
            query = "구로구 구로동",
            modifier = Modifier
                .align(Alignment.TopCenter),
        )
        BottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter),
        )
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
private fun BottomBar(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
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
                TabRow(
                    tab = tab,
                )
            }
        }
    }
}

@Composable
private fun RowScope.TabRow(
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
private fun MainKakaoMap(
    modifier: Modifier = Modifier,
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
    )
}