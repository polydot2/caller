package com.poly.caller.presentation.home

sealed interface HomeEventFromVM {
    data class NavigateTo(val index: Int) : HomeEventFromVM
}
