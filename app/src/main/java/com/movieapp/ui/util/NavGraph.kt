package com.movieapp.ui.util



sealed class Screen{
    object HomeScreen: Screen()
    object SearchScreen: Screen()
}