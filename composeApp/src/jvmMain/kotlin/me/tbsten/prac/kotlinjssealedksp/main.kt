package me.tbsten.prac.kotlinjssealedksp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kotlin JS Sealed KSP Practice",
    ) {
        App()
    }
}