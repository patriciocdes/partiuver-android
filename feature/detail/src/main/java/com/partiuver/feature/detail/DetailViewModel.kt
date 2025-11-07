package com.partiuver.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partiuver.domain.common.AppError
import com.partiuver.domain.usecase.GetMovieDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetail: GetMovieDetailUseCase
) : ViewModel() {

    private val movieId: String = checkNotNull(savedStateHandle["movieId"])

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.Refresh,
            is DetailEvent.Load -> load()
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }

        runCatching { getMovieDetail(movieId) }
            .onSuccess { movie ->
                if (movie == null)
                    _state.update { it.copy(isLoading = false, error = AppError.NotFound) }
                else
                    _state.update { it.copy(isLoading = false, movie = movie) }
            }
            .onFailure { e ->
                _state.update { it.copy(isLoading = false, error = AppError.Unknown(e)) }
            }
    }
}