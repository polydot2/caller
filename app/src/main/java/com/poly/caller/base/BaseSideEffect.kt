package com.poly.caller.base


sealed interface BaseEventFromVM {
    data object ExitApplication : BaseEventFromVM
}
