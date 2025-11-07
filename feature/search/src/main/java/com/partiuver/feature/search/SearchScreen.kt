package com.partiuver.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partiuver.core.ui.common.messageRes
import com.partiuver.core.ui.components.ErrorState
import com.partiuver.core.ui.components.LoadingState
import com.partiuver.core.ui.components.PosterImage
import com.partiuver.core.ui.components.SearchField
import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.formatRuntime
import com.partiuver.domain.model.formatTomato
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onOpenDetail: (String) -> Unit, searchViewModel: SearchViewModel = hiltViewModel()
) {
    val state by searchViewModel.state.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val onSearch = {
        keyboardController?.hide()
        searchViewModel.onEvent(SearchEvent.Submit)

        //TODO(Rever este comentário)
        //searchViewModel.onEvent(SearchEvent.QueryChanged(""))
    }

    LaunchedEffect(Unit) {
        searchViewModel.effects.collectLatest { effect ->
            when (effect) {
                is SearchEffect.NavigateToDetail -> onOpenDetail(effect.id)
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            val errorMessage = context.getString(it.messageRes())
            snackBarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.partiuver_title)) }) },
        snackbarHost = { SnackbarHost(snackBarHostState) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                SearchField(
                    query = state.query,
                    onQueryChange = { searchViewModel.onEvent(SearchEvent.QueryChanged(it)) },
                    onSubmit = onSearch,
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.search_movie)
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = onSearch,
                    enabled = state.query.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .defaultMinSize(minWidth = 90.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(stringResource(R.string.search))
                }
            }

            if (state.isLoading && state.items.isEmpty()) {
                LoadingState(message = stringResource(R.string.loading_movies))
            } else if (state.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            when {
                state.error != null && state.items.isEmpty() && !state.isLoading ->
                    ErrorState(
                        message = stringResource(state.error.messageRes()),
                        onRetry = { searchViewModel.onEvent(SearchEvent.Submit) }
                    )

                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(state.items, key = { it.id }) { movie ->
                        MovieRowCard(
                            movie = movie,
                            onClick = {
                                searchViewModel.onEvent(SearchEvent.OpenDetail(movie.id))
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieRowCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterImage(
                url = movie.photoUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 64.dp, height = 88.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = "${movie.title} (${movie.year})",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                val left = movie.formatRuntime()
                val right = movie.formatTomato()
                val line = listOf(left, right).filter { it.isNotBlank() }.joinToString("   ")

                if (line.isNotBlank()) {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}