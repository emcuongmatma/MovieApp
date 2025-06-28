package com.movieapp.ui.movie_search.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.movieapp.ui.movie_list.components.MovieItem
import com.movieapp.ui.movie_search.MovieSearchState
import com.movieapp.ui.theme.netflix_gray_2

fun LazyGridScope.movieSearchInit(
    state: MovieSearchState,
    onItemClicked:(String)->Unit
){
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(
            "Nhập từ khoá để tìm kiếm phim",
            style = MaterialTheme.typography.titleMedium.copy(netflix_gray_2),
            textAlign = TextAlign.Center
        )
    }
    item(span = { GridItemSpan(maxLineSpan) }){
        Text(
            "Tìm kiếm gần đây",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.White.copy(alpha = 0.7f)),
            textAlign = TextAlign.Start
        )
    }
    items(items = state.recentlySearch, key = { it-> it.slug}) {
        MovieItem(
            movie = it
        ) { slug,source ->
            onItemClicked(slug)
        }
    }
}