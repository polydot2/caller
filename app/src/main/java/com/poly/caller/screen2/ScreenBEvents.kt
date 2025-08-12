package com.poly.caller.screen2

sealed interface ScreenBEvent {
    data object ExitApplication : ScreenBEvent
}
