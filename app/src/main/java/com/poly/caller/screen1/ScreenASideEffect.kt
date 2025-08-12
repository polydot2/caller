package com.poly.caller.screen1

sealed interface ScreenAEventFromVM {
    data object ExitApplication : ScreenAEventFromVM
}
