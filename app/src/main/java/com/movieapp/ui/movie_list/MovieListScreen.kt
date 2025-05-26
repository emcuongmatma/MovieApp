@file:Suppress("DEPRECATION")

package com.movieapp.ui.movie_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.movieapp.R
import com.movieapp.data.datasource.remote.MovieSourceManager
import com.movieapp.ui.movie_list.components.MovieRow

@Composable
fun MovieListScreen(
    mainState: MovieListState,
    onItemSelected: (String) -> Unit,
    onSourceClicked: () -> Unit,
    onMoreClicked: (String) -> Unit,
    onRefresh:()-> Unit
) {
    val painterSource = when (mainState.movieSource) {
        MovieSourceManager.MovieSource.KKPhim -> R.drawable.movie_background_horizontal
        MovieSourceManager.MovieSource.NguonC -> R.drawable.logonc
        else -> R.drawable.logoophim
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(painterSource),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(60.dp)
                .clickable {
                    onSourceClicked()
                },
            contentScale = ContentScale.FillHeight
        )
        SwipeRefresh(
            state = rememberSwipeRefreshState(mainState.isRefreshing),
            onRefresh = {
                onRefresh()
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
                        text = "Phim mới cập nhật",
                        list = mainState.recentlyUpdateList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("phim-moi-cap-nhat") })
                }
                item {
                    MovieRow(
                        text = "Phim bộ mới",
                        list = mainState.newSeriesList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("phim-bo") })
                }
                item {
                    MovieRow(
                        text = "Phim lẻ mới",
                        list = mainState.newStandaloneFilmList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("phim-le") })
                }
                item {
                    MovieRow(
                        text = "TV Shows",
                        list = mainState.newTvShowList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("tv-shows") })
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}