package io.hlab.sample.app.presentation.home

import androidx.annotation.StringRes
import io.hlab.sample.app.R

enum class HomeTab(@StringRes val titleResId: Int) {
    Home(titleResId = R.string.home_tab_home),

    NearBy(titleResId = R.string.home_tab_nearby),

    Profile(titleResId = R.string.home_tab_profile),
}