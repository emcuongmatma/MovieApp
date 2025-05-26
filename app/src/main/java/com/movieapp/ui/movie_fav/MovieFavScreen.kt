@file:Suppress("DEPRECATION")

package com.movieapp.ui.movie_fav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.movieapp.ui.movie_list.MovieListState
import com.movieapp.ui.movie_list.composable.MovieRow

@Composable
fun MovieFavScreen(
    state: MovieListState,
    onItemSelected: (String,Int?) -> Unit,
    onMoreClicked: (String) -> Unit,
    onLoadFavMovies:()->Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(state.isRefreshing),
        onRefresh = {
            onLoadFavMovies()
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .statusBarsPadding()
            .padding(top = 10.dp, start = 5.dp, end = 5.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 10.dp, bottom = 60.dp)
                .fillMaxWidth()
                .background(color = Color.Black),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                MovieRow(
                    text = "Tiếp tục xem",
                    list = state.resMovieList,
                    onItemSelected = {slug,source->onItemSelected(slug,source)},
                    onMoreClicked = { onMoreClicked("lich-su") })
            }
            item {
                MovieRow(
                    text = "Yêu thích",
                    list = state.favMovieList,
                    onItemSelected = {slug,source->onItemSelected(slug,source)},
                    onMoreClicked = { onMoreClicked("yeu-thich") })
            }
        }
    }
}