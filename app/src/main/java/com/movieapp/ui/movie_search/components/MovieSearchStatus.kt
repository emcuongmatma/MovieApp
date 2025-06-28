package com.movieapp.ui.movie_search.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.movieapp.ui.movie_detail.components.CustomCircularProgress
import com.movieapp.ui.movie_search.MovieSearchState
import com.movieapp.ui.theme.netflix_gray_2
import com.movieapp.ui.util.LoadStatus

@Composable
fun MovieSearchStatus(
    state: MovieSearchState,
    onMoreResult:() ->Unit
){
    when (state.status) {
        is LoadStatus.Loading -> {
            CustomCircularProgress()
        }
        is LoadStatus.Success -> {
            Button(
                onClick = {
                    onMoreResult()
                },
                colors = ButtonDefaults.textButtonColors()
                    .copy(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier.padding(start = 30.dp, end = 30.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "Hiển thị thêm kết quả",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

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