package com.poly.mylibrary

sealed interface CameraEventFromVM {
    data object ExitApplication : CameraEventFromVM
}
