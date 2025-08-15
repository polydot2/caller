package com.poly.caller.presentation.base

import com.poly.caller.model.Configuration

data class BaseScreenState(
    val isLoading: Boolean,
    val config: Configuration?, // current configuration
    val isModified: Boolean,
    val configurationsName: List<String>,
    val showSaveAsDialog: Boolean,
)
