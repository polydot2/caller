package com.poly.caller.presentation.base

import com.poly.caller.model.ExtraInput

sealed interface BaseEvent {
    data object ExitApplication : BaseEvent
    data class UpdateExtraInput(val key: String, val value: ExtraInput) : BaseEvent
    data class LoadAnotherCOnfiguration(val configurationToLoad: String) : BaseEvent
    data class SaveAs(val name: String) : BaseEvent
    data object Reset : BaseEvent
    data object ShowSaveAsDialog : BaseEvent
    data object DissmisSaveAsDialog : BaseEvent
}
