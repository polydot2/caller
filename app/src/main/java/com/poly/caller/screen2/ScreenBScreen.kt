package com.poly.caller.screen2

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.poly.caller.base.BaseScreen
import com.poly.caller.screen1.ScreenAViewModel

@Composable
fun ScreenBScreen(
    viewModel: ScreenBViewModel = hiltViewModel(),
) {
    return BaseScreen(viewModel)
}