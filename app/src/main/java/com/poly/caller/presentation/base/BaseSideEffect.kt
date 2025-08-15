package com.poly.caller.presentation.base


sealed interface BaseEventFromVM {
    data object ExitApplication : BaseEventFromVM
}
