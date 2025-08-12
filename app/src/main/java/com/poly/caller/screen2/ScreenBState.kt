package com.poly.caller.screen2

import com.poly.caller.model.Configuration
import com.poly.caller.model.Configurations

data class ScreenBScreenState(
    val isLoading: Boolean,
    val config: Pair<String, Configuration?>?
)
