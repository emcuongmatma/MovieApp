package com.movieapp.ui.movie_detail.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movieapp.R
import com.movieapp.domain.model.moviedetail.toActorString
import com.movieapp.ui.movie_detail.MovieDetailState

@Composable
fun MovieDetails(
    state: MovieDetailState?,
    onSeverSelected: (Int) -> Unit,
    onEpSelected: (String) -> Unit
) {
    var isShowFull by remember {
        mutableStateOf(false)
    }
    val gridState = rememberLazyGridState()
    Column(
        modifier = Modifier
            .background(Color(33, 33, 33))
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = state?.movie?.movie!!.name.toString(),
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.movie.movie.year.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Box(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .size(30.dp, 30.dp)
                    .border(width = 1.dp, shape = RectangleShape, color = colorResource(R.color.netflix_white_30)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.movie.movie.quality.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }
            Text(
                text = state.movie.movie.time.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Box(
                modifier = Modifier
                    .background(color = colorResource(R.color.netflix_white_15)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.movie.movie.episodeCurrent.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(color = colorResource(R.color.netflix_red)),
                    maxLines = 1
                )
            }
            Text(
                text = state.movie.movie.country!!.first().name.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
        Text(
            text = state.movie.movie.content.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            maxLines = if (!isShowFull) 3 else 10,
            overflow = TextOverflow.Clip,
            modifier = Modifier.clickable {
                isShowFull = !isShowFull
            }
        )
        Text(
            text = state.movie.movie.actor.toActorString(),
            style = MaterialTheme.typography.bodySmall.copy(color = colorResource(R.color.netflix_black )),
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Chọn Server",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                fontWeight = FontWeight.Bold
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                for (i in state.movie.episodes.indices) {
                    ServerItem(
                        name = state.movie.episodes[i].serverName!!,
                        index = i,
                        serverSelected = state.serverSelected
                    ) {
                        onSeverSelected(it)
                    }
                }
            }
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(9),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                state = gridState
            ) {
                items(items = state.movie.episodes[state.serverSelected].serverData) { item ->
                    EpItem(
                        ep = item.name!!,
                        epSelected = state.epSelected
                    ) {
                        onEpSelected(item.name)
                    }
                }
            }
        }
    }
}