package com.partiuver.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.partiuver.core.ui.components.LoadingState
import com.partiuver.core.ui.components.PosterImage
import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.formatRuntime
import com.partiuver.domain.model.formatTomato
import com.partiuver.core.ui.common.messageRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBack: () -> Unit,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    val state by detailViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        detailViewModel.onEvent(DetailEvent.Load)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.more_about_film)) }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_text)
                    )
                }
            })
        }) { padding ->
        when {
            state.isLoading -> LoadingState(
                modifier = Modifier.padding(padding),
                message = stringResource(R.string.loading_details)
            )

            state.error != null -> {
                val errorMessage = stringResource(id = state.error.messageRes())

                ErrorBox(
                    message = errorMessage,
                    onRetry = { detailViewModel.onEvent(DetailEvent.Refresh) },
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            else -> state.movie?.let { movie ->
                DetailContent(
                    movie = movie,
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun DetailContent(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        PosterImage(
            url = movie.backdropUrl ?: movie.photoUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${movie.title} (${movie.year})",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        val left = movie.formatRuntime()
        val right = movie.formatTomato()
        val meta = listOf(left, right).filter { it.isNotBlank() }.joinToString("      ")

        if (meta.isNotBlank()) {
            Text(text = meta, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.where_to_watch),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        if (movie.offers.isEmpty()) {
            Text(
                stringResource(R.string.no_offers_available),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            val uri = LocalUriHandler.current

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                movie.offers.forEach { offer ->
                    OfferRow(
                        type = offer.type,
                        name = offer.name,
                        onOpen = { uri.openUri(offer.url) }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun OfferRow(
    name: String,
    type: String,
    onOpen: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                shape = RoundedCornerShape(14.dp),
                onClick = onOpen
            ) {
                Text(stringResource(R.string.check_offer))
            }
        }
    }
}

@Composable
fun ErrorBox(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.try_again)) }
    }
}