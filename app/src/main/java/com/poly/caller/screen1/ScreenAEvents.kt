package com.poly.caller.screen1

sealed interface ScreenAEvent {
    data object ExitApplication : ScreenAEvent
    data class UpdateExtraInput(val key: String, val value: Any) : ScreenAEvent
    data class LoadAnotherCOnfiguration(val configurationToLoad: String) : ScreenAEvent
    data class SaveAs(val name: String) : ScreenAEvent
    data object Reset : ScreenAEvent
    data object ShowSaveAsDialog: ScreenAEvent
    data object DissmisSaveAsDialog: ScreenAEvent
}
