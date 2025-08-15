package com.poly.caller.presentation.home

sealed interface HomeEvent {
    data class NavigateTo(val index: Int) : HomeEvent
}
