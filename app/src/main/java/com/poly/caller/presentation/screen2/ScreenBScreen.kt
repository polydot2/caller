package com.poly.caller.presentation.screen2

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.poly.caller.presentation.base.BaseScreen

@Composable
fun ScreenBScreen(
    viewModel: ScreenBViewModel = hiltViewModel(),
) {
    return BaseScreen(viewModel)
}