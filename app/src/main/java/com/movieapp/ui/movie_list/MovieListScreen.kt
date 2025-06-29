

package com.movieapp.ui.movie_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

@Suppress("DEPRECATION")
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
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(painterSource),
            contentDescription = null,
            modifier = Modifier
                .size(width = 200.dp, height = 80.dp)
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onSourceClicked()
                },
            contentScale = ContentScale.FillWidth
        )
        SwipeRefresh(
            state = rememberSwipeRefreshState(mainState.isRefreshing),
            onRefresh = {
                onRefresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .padding(start = 5.dp, end = 5.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .fillMaxWidth()
                    .background(color = Color.Black),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(key = "pbhq"){
                    MovieRow(
                        text = "Phim Hàn Quốc mới",
                        list = mainState.newKRSeriesList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("phim-bo-hq") })
                }
                item(key = "pbtq"){
                    MovieRow(
                        text = "Phim Trung Quốc mới",
                        list = mainState.newCNSeriesList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("phim-bo-tq") })
                }
                item(key = "pbusuk"){
                    MovieRow(
                        text = "Phim US-UK mới",
                        list = mainState.newUSUKSeriesList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("phim-bo-usuk") })
                }
                item(key = "plm"){
                    MovieRow(
                        text = "Phim lẻ",
                        list = mainState.newStandaloneFilmList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("phim-le") })
                }
                item(key = "tvs"){
                    MovieRow(
                        text = "TV Shows",
                        list = mainState.newTvShowList,
                        onItemSelected = { slug, source ->
                            onItemSelected(slug)
                        },
                        onMoreClicked = { onMoreClicked("tv-shows") })
                }
                item{
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}