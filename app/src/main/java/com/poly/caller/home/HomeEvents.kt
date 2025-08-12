package com.poly.caller.home

sealed interface HomeEvent {
    data class NavigateTo(val index: Int) : HomeEvent
}
