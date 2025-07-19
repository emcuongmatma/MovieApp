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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.movieapp.ui.movie_search.MovieSearchViewModel
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
    val context = LocalContext.current
    SideEffect {
        systemUiController.setStatusBarColor(Color.Transparent, darkIcons = false)
    }
    val movieDetailViewModel = hiltViewModel<MovieDetailViewModel>()
    val movieSearchViewModel = hiltViewModel<MovieSearchViewModel>()
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
                .statusBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            when (mainState.screen) {
                Screen.HomeScreen -> {
                    MovieListScreen(
                        viewModel = mainViewModel,
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
                        viewModel = movieSearchViewModel,
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
                            mainViewModel.changeSource(source!!)
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
                    viewModel = mainViewModel,
                    state = mainState,
                    onExit = { mainViewModel.isGridListOpen(false) },
                    onItemClicked = {
                        onItemSelected(scope, bottomSheetState, movieDetailViewModel, it)
                    },
                    onMoreResult = {  },
                    onClear = {
                        mainViewModel.onClear(it)
                    })
            }
        }
    }
    LaunchedEffect(Unit) {
        listOf(movieSearchViewModel,movieDetailViewModel,mainViewModel).forEach { vm ->
            launch {
                vm.eventFlow.collect { event ->
                    when (event) {
                        is UiEvent.ShowToast -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
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
