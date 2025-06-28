package com.movieapp.ui.movie_search.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import com.movieapp.data.model.custom.CustomMovieModel
import com.movieapp.ui.movie_list.components.MovieItem
import com.movieapp.ui.movie_search.MovieSearchState
import com.movieapp.ui.theme.netflix_gray_2


fun LazyGridScope.movieSearchResult(
    state: MovieSearchState,
    onMoreResult:()->Unit,
    onItemSelected:(CustomMovieModel,String)->Unit
){
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(
            "Kết quả cho \"${state.searchKey}\"",
            style = MaterialTheme.typography.titleMedium.copy(netflix_gray_2),
            textAlign = TextAlign.Start
        )
    }
    items(items = state.movieSearchList, key = {it-> it.slug}) {
        MovieItem(
            movie = it
        ) { slug,source ->
//            focusManager.clearFocus()
//            viewModel.addSearchHistory(it)
//            onItemClicked(slug)
            onItemSelected(it,slug)
        }
    }
    item(span = { GridItemSpan(maxLineSpan) }) {
        MovieSearchStatus(
            state = state,
            onMoreResult = {
                onMoreResult()
            }
        )
    }
}