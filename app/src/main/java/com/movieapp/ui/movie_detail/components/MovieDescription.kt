package com.movieapp.ui.movie_detail.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movieapp.ui.movie_detail.MovieDetailState
import com.movieapp.ui.theme.netflix_black
import com.movieapp.ui.theme.netflix_gray
import com.movieapp.ui.theme.netflix_red
import com.movieapp.ui.theme.netflix_white_15
import com.movieapp.ui.theme.netflix_white_30
import com.movieapp.ui.util.convertContent

@Composable
fun MovieDetails(
    state: MovieDetailState,
    onSeverSelected: (Int) -> Unit,
    onEpSelected: (String) -> Unit,
    addFavMovie:()->Unit
) {
    var isShowFull by remember {
        mutableStateOf(false)
    }
    val gridState = rememberLazyGridState()
    Column(
        modifier = Modifier
            .background(netflix_black)
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = state.movie.movie?.name.toString(),
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.movie.movie?.year.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Box(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .size(30.dp, 30.dp)
                    .border(width = 1.dp, shape = RectangleShape, color = netflix_white_30),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.movie.movie?.quality.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }
            Text(
                text = state.movie.movie?.time.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Box(
                modifier = Modifier
                    .background(color = netflix_white_15),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.movie.movie?.episodeCurrent.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(color = netflix_red),
                    maxLines = 1
                )
            }
            IconButton(onClick = {
                addFavMovie()
            }, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (state.isFav) netflix_red else netflix_white_30,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text(
            text = state.movie.movie?.content.toString().convertContent(),
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            textAlign = TextAlign.Justify,
            maxLines = if (!isShowFull) 3 else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable {
                isShowFull = !isShowFull
            }
        )
        Text(
            text = "Cast: " + if (state.movie.movie?.casts!!.isNotEmpty()) state.movie.movie.casts else state.movie.movie.actor.joinToString(
                ", "
            ),
            style = MaterialTheme.typography.bodySmall.copy(color = netflix_gray),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Director: " + state.movie.movie.director.joinToString(", "),
            style = MaterialTheme.typography.bodySmall.copy(color = netflix_gray),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Chọn Server",
            style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
            fontWeight = FontWeight.Bold
        )
        state.movie.episodes?.let {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
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