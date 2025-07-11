package com.movieapp.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.movieapp.ui.movie_detail.MovieDetailScreen
import com.movieapp.ui.movie_detail.MovieDetailViewModel
import com.movieapp.ui.movie_fav.MovieFavScreen
import com.movieapp.ui.movie_list.MovieListByType
import com.movieapp.ui.movie_list.MovieListScreen
import com.movieapp.ui.movie_list.MovieListViewModel
import com.movieapp.ui.movie_list.components.BottomNavigation
import com.movieapp.ui.movie_list.components.SourceManagerDialog
import com.movieapp.ui.movie_search.MovieSearchScreen
import com.movieapp.ui.util.LoadStatus
import com.movieapp.ui.util.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MovieListViewModel,
    onPlayerViewReady: (PlayerView) -> Unit,
    onPIP: (Boolean) -> Unit
) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Color.Transparent, darkIcons = false)
    }
    val movieDetailViewModel = hiltViewModel<MovieDetailViewModel>()
    val mainState by mainViewModel.state.collectAsStateWithLifecycle()
    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val scope = rememberCoroutineScope()
    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = bottomSheetState,
        sheetShape = RectangleShape,
        sheetMaxWidth = LocalConfiguration.current.screenWidthDp.dp,
        sheetSwipeEnabled = false,
        sheetPeekHeight = 0.dp,
        sheetDragHandle = null,
        sheetContent = {
            BackHandler(
                enabled = bottomSheetState.bottomSheetState.isVisible
            ) {
                movieDetailViewModel.pausePlayer()
                movieDetailViewModel.reset()
                movieDetailViewModel.isFullScreen(false)
                onExit(scope, bottomSheetState)
            }
            MovieDetailScreen(
                viewModel = movieDetailViewModel,
                onPlayerViewReady = onPlayerViewReady,
                onPIP = onPIP
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            when (mainState.screen) {
                Screen.HomeScreen -> {
                    MovieListScreen(
                        mainState = mainState,
                        onItemSelected = {
                            onItemSelected(scope, bottomSheetState, movieDetailViewModel, it)
                        },
                        onSourceClicked = {
                            mainViewModel.setSourceManagerOpen(true)
                        },
                        onMoreClicked = { slug ->
                            mainViewModel.setTypeSlug(slug)
                            mainViewModel.isGridListOpen(true)
                        },
                        onRefresh = {
                            mainViewModel.fetchAllMovies()
                        })
                }

                Screen.SearchScreen -> {
                    MovieSearchScreen(
                        onItemClicked = {
                            movieDetailViewModel.setRecentlySearch()
                            onItemSelected(scope, bottomSheetState, movieDetailViewModel, it)
                        }
                    )
                }

                Screen.FavouriteScreen -> {
                    MovieFavScreen(
                        state = mainState,
                        onItemSelected = { slug, source ->
                            mainViewModel.changeSource(source!!.toInt())
                            onItemSelected(scope, bottomSheetState, movieDetailViewModel, slug)
                        },
                        onMoreClicked = { slug ->
                            mainViewModel.setTypeSlug(slug)
                            mainViewModel.isGridListOpen(true)
                        },
                        onLoadFavMovies = {
                            mainViewModel.loadFavMovies()
                        })
                }
            }
            BottomNavigation(mainState = mainState, setScreen = { screen ->
                mainViewModel.setScreen(screen)
            })
            BackHandler(
                enabled = mainState.isOpenGridList
            ) {
                mainViewModel.isGridListOpen(false)
            }
            AnimatedVisibility(
                visible = mainState.isOpenGridList,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                ) + fadeIn(initialAlpha = 1f),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                ) + fadeOut(targetAlpha = 1f)
            ) {
                MovieListByType(
                    state = mainState,
                    onExit = { mainViewModel.isGridListOpen(false) },
                    onItemClicked = {
                        onItemSelected(scope, bottomSheetState, movieDetailViewModel, it)
                    },
                    onMoreResult = { mainViewModel.getMoreResult() },
                    onClear = {
                        mainViewModel.onClear(it)
                    })
            }
        }
    }
    if (mainState.status is LoadStatus.Error) {
        Toast.makeText(LocalContext.current, "Lỗi kết nối !", Toast.LENGTH_SHORT).show()
        mainViewModel.reset()
    }
    if (mainState.isSourceManagerOpen) {
        SourceManagerDialog(
            onDismissRequest = { mainViewModel.setSourceManagerOpen(false) },
            onSource = {
                mainViewModel.changeSource(it)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun onExit(
    scope: CoroutineScope,
    bottomSheetState: BottomSheetScaffoldState
) {
    scope.launch {
        bottomSheetState.bottomSheetState.hide()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun onItemSelected(
    scope: CoroutineScope,
    bottomSheetState: BottomSheetScaffoldState,
    movieDetailViewModel: MovieDetailViewModel,
    it: String
) {
    movieDetailViewModel.setSlug(it)
    movieDetailViewModel.getMovieDetail()
    scope.launch {
        bottomSheetState.bottomSheetState.expand()
    }
}
