package io.hlab.sample.app.presentation.home

import dagger.hilt.android.lifecycle.HiltViewModel
import io.hlab.sample.app.presentation.base.StateViewModel
import io.hlab.sample.app.presentation.base.ViewModelState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : StateViewModel<HomeState>(initialState = HomeState()) {

    fun toggleBarShowingState() = withState { state ->
        setState { copy(isMenuBarShowing = !state.isMenuBarShowing) }
    }
}

data class HomeState(
    val isMenuBarShowing: Boolean = true,
) : ViewModelState