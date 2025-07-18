package com.movieapp.ui.movie_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.movieapp.ui.movie_search.components.movieSearchInit
import com.movieapp.ui.movie_search.components.movieSearchResult
import com.movieapp.ui.theme.netflix_gray_2
import com.movieapp.ui.theme.searchbar_background
import com.movieapp.ui.util.LoadStatus


@Composable
fun MovieSearchScreen(
    viewModel: MovieSearchViewModel,
    onItemClicked: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val movies = viewModel.pagingFLow.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.Black),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TextField(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.9f),
                value = state.searchKey,
                onValueChange = {
                    viewModel.updateSearchKey(it)
                },
                textStyle = TextStyle.Default.copy(color = Color.White),
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = netflix_gray_2
                    )
                },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = searchbar_background,
                    unfocusedContainerColor = searchbar_background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(5.dp),
                singleLine = true,
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )

            )
        }
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 70.dp, start = 5.dp, end = 5.dp),
            columns = GridCells.Adaptive(minSize = 110.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (state.status) {
                is LoadStatus.Init -> {
                    movieSearchInit(
                        recentlySearchList = state.recentlySearch,
                        onItemClicked = {
                            focusManager.clearFocus()
                            onItemClicked(it)
                        }
                    )
                }

                is LoadStatus.Error -> {
                }

                else -> {
                    movieSearchResult(
                        searchKey = state.searchKey,
                        movies = movies,
                        setError = { err ->
                            viewModel.setToast(err.message.toString())
                        },
                        onItemSelected = { movie, slug ->
                            focusManager.clearFocus()
                            viewModel.addSearchHistory(movie)
                            onItemClicked(slug)
                        }
                    )
                }
            }
        }
    }
}


