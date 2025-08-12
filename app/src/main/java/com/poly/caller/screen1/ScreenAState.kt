package com.poly.caller.screen1

import com.poly.caller.model.Configuration

data class ScreenAScreenState(
    val isLoading: Boolean,
    val config: Configuration?, // current configuration
    val isModified: Boolean,
    val configurationsName: List<String>,
    val showSaveAsDialog: Boolean,
)
