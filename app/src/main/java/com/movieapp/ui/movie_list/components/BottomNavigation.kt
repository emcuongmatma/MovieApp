package com.movieapp.ui.movie_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.movieapp.ui.movie_list.MovieListState
import com.movieapp.ui.theme.netflix_black
import com.movieapp.ui.util.Screen

@Composable
fun BottomNavigation (
    mainState: MovieListState,
    setScreen:(Screen)->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(netflix_black)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            isSelected = mainState.screen is Screen.HomeScreen,
            imageVector = Icons.Default.Home,
            onScreenChange = {
                setScreen(Screen.HomeScreen)
            }
        )
        BottomNavItem(
            isSelected = mainState.screen is Screen.SearchScreen,
            imageVector = Icons.Filled.Search,
            onScreenChange = {
                setScreen(Screen.SearchScreen)
            }
        )
        BottomNavItem(
            isSelected = mainState.screen is Screen.FavouriteScreen,
            imageVector = Icons.Filled.Favorite,
            onScreenChange = {
                setScreen(Screen.FavouriteScreen)
            }
        )
    }
}