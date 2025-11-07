package com.partiuver.feature.search

import com.partiuver.domain.common.AppError
import com.partiuver.domain.model.Movie

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val items: List<Movie> = emptyList(),
    val error: AppError? = null
)

sealed interface SearchEvent {
    data class QueryChanged(val value: String) : SearchEvent
    data object Submit : SearchEvent
    data class OpenDetail(val id: String) : SearchEvent
}

sealed interface SearchEffect {
    data class NavigateToDetail(val id: String) : SearchEffect
}