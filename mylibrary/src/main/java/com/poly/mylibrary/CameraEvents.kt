package com.poly.mylibrary

sealed interface CameraEvent {
    data object ExitApplication :CameraEvent
}
