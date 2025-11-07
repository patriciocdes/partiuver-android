package com.partiuver.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partiuver.domain.common.AppError
import com.partiuver.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    private val _effects = Channel<SearchEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> _state.update { it.copy(query = event.value) }
            is SearchEvent.Submit -> submit()
            is SearchEvent.OpenDetail -> navigateToDetail(event.id)
        }
    }

    private fun navigateToDetail(id: String) = viewModelScope.launch {
        _effects.send(SearchEffect.NavigateToDetail(id))
    }

    private fun submit() = viewModelScope.launch {
        val query = state.value.query.trim()

        if (query.isBlank()) {
            _state.update { it.copy(items = emptyList(), error = null) }
            return@launch
        }

        _state.update { it.copy(isLoading = true, error = null) }

        runCatching { searchMovies(query) }
            .onSuccess { list ->
                _state.update { it.copy(isLoading = false, items = list, error = null) }
            }
            .onFailure { e ->
                _state.update { it.copy(isLoading = false, error = AppError.Unknown(e)) }
            }
    }
}