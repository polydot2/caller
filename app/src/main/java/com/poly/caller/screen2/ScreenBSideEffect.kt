package com.poly.caller.screen2

sealed interface ScreenBEventFromVM {
    data object ExitApplication : ScreenBEventFromVM
}
