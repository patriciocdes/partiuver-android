package com.partiuver.feature.detail

import com.partiuver.domain.common.AppError
import com.partiuver.domain.model.Movie

data class DetailState(
    val isLoading: Boolean = true,
    val movie: Movie? = null,
    val error: AppError? = null
)

sealed interface DetailEvent {
    data object Refresh : DetailEvent
    data object Load : DetailEvent
}

//TODO(Rever este Efeito)
sealed interface DetailEffect