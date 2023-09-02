package io.hlab.sample.app.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.hlab.sample.app.presentation.home.view.MainMenuBarSection
import io.hlab.sample.app.presentation.home.view.MapSection

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    HomeScreen(
        state = state,
        modifier = modifier,
        toggleBars = viewModel::toggleBarShowingState,
    )
}

@Composable
private fun HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    toggleBars: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
    ) { contentPadding ->

        Box(modifier = Modifier.padding(contentPadding)) {
            MapSection(
                modifier = Modifier.fillMaxSize(),
                toggleBars = toggleBars,
            )
            MainMenuBarSection(
                query = "분당구 판교동",
                isShowing = state.isMenuBarShowing,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}