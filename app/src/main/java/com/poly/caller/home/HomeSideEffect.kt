package com.poly.caller.home

sealed interface HomeEventFromVM {
    data class NavigateTo(val index: Int) : HomeEventFromVM
}
