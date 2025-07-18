package com.movieapp.ui

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}