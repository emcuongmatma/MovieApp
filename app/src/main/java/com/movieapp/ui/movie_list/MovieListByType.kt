package com.movieapp.ui.movie_list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.ui.movie_detail.components.CustomCircularProgress
import com.movieapp.ui.movie_list.components.MovieItem
import com.movieapp.ui.util.LoadStatus

@Composable
fun MovieListByType(
    viewModel: MovieListViewModel,
    onExit: () -> Unit,
    state: MovieListState,
    onItemClicked: (String) -> Unit,
    onMoreResult: () -> Unit,
    onClear: (String) -> Unit
) {
    BackHandler {
        onExit()
    }
    var title = ""
    var list: LazyPagingItems<CustomMovieModel>? = null
    when (state.typeSlug) {
        "phim-bo-hq" -> {
            list = viewModel.pagingSeriesKR.collectAsLazyPagingItems()
            title = "Phim Hàn Quốc mới"
        }

        "phim-bo-tq" -> {
            list = viewModel.pagingSeriesCN.collectAsLazyPagingItems()
            title = "Phim Trung Quốc mới"
        }

        "phim-bo-usuk" -> {
            list = viewModel.pagingSeriesUSUK.collectAsLazyPagingItems()
            title = "Phim US-UK mới"
        }

        "phim-le" -> {
            list = viewModel.pagingMovies.collectAsLazyPagingItems()
            title = "Phim lẻ"
        }

        "tv-shows" -> {
            list = viewModel.pagingTVS.collectAsLazyPagingItems()
            title = "Tv Shows"
        }

        "lich-su" -> {
            title = "Lịch sử xem"
        }

        "yeu-thich" -> {
            title = "Phim yêu thích"
        }

        else -> {
            listOf<CustomMovieModel>()
        }
    }
    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { onExit() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp, bottom = 5.dp),
            columns = GridCells.Adaptive(minSize = 110.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                count = when (state.typeSlug) {
                    "lich-su" -> state.resMovieList.size
                    "yeu-thich" -> state.favMovieList.size
                    else -> list!!.itemCount
                }
            ) {
                MovieItem(
                    movie =  when (state.typeSlug) {
                        "lich-su" -> state.resMovieList[it]
                        "yeu-thich" -> state.favMovieList[it]
                        else -> list!![it]
                    },
                    onItemSelected = { slug, source ->
                        onItemClicked(slug)
                    }
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                when (state.status) {
                    is LoadStatus.Loading -> {
                        CustomCircularProgress()
                    }
                    else -> {
                        Button(
                            onClick = {
                                when (state.typeSlug) {
                                    "lich-su" -> onClear("r")
                                    "yeu-thich" -> onClear("f")
                                    else -> onMoreResult()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors()
                                .copy(containerColor = Color.White, contentColor = Color.Black),
                            modifier = Modifier.padding(start = 30.dp, end = 30.dp),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text(
                                text = when (state.typeSlug) {
                                    "lich-su" -> "Xoá lịch sử xem"
                                    "yeu-thich" -> "Xoá danh sách yêu thích"
                                    else -> "Hiển thị thêm kết quả"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}