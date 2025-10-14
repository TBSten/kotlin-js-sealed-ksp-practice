@file:OptIn(ExperimentalJsExport::class)

package me.tbsten.prac.kotlinjssealedksp

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
sealed interface MyScreenState

@JsExport
data object LoadingState : MyScreenState

@JsExport
data class SuccessState(
    val data: String,
) : MyScreenState

@JsExport
data class ErrorState(
    val error: Throwable,
) : MyScreenState
