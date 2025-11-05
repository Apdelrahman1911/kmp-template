package me.onvo.onvo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.onvo.onvo.di.initKoin

fun main()  {
    initKoin() // start Koin for desktop
     application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ONVO",
    ) {
        App()
    }
}}