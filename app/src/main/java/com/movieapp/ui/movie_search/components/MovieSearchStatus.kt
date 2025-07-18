package com.movieapp.ui.movie_search.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.LoadState
import com.movieapp.ui.movie_detail.components.CustomCircularProgress
import com.movieapp.ui.theme.netflix_gray_2

@Composable
fun MovieSearchStatus(
    lazyState: LoadState,
    setError:(Throwable) ->Unit
){
    when (lazyState) {
        is LoadState.Loading -> {
            CustomCircularProgress()
        }
        is LoadState.Error ->{
            setError(lazyState.error)
        }
        else -> {
            Text(
                "Không còn kết quả nào khác.",
                style = MaterialTheme.typography.titleMedium.copy(netflix_gray_2),
                textAlign = TextAlign.Center
            )
        }
    }
}