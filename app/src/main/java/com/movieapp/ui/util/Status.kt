package com.movieapp.ui.util

sealed class LoadStatus(val description: String=""){
    class Init : LoadStatus()
    class Loading : LoadStatus()
    class Error(message:String): LoadStatus(message)
    class Success: LoadStatus()
}