package com.movieapp.ui.movie_search.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.compose.LazyPagingItems
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.ui.movie_list.components.MovieItem
import com.movieapp.ui.theme.netflix_gray_2

fun LazyGridScope.movieSearchResult(
    searchKey:String,
    movies: LazyPagingItems<CustomMovieModel>,
    setError:(Throwable) ->Unit,
    onItemSelected:(CustomMovieModel,String)->Unit
){
    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
        Text(
            "Kết quả cho \"${searchKey}\"",
            style = MaterialTheme.typography.titleMedium.copy(netflix_gray_2),
            textAlign = TextAlign.Start
        )
    }
    items(
        count = movies.itemCount,
        key = { it },
    ) {
        movies[it]?.let { it->
            MovieItem(
                movie = it
            ) { slug,source ->
                onItemSelected(it,slug)
            }
        }
    }
    item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
        MovieSearchStatus(
            lazyState = movies.loadState.refresh,
            setError = setError
        )
    }
}