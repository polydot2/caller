package com.poly.caller.presentation.screen1

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.poly.caller.presentation.base.BaseScreen

@Composable
fun ScreenAScreen(
    viewModel: ScreenAViewModel = hiltViewModel(),
) {
    return BaseScreen(viewModel)
}