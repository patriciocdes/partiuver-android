package com.partiuver.domain.usecase

import com.partiuver.domain.model.Movie
import com.partiuver.domain.repository.MovieRepository

class SearchMoviesUseCase(private val repository: MovieRepository) {

    suspend operator fun invoke(query: String): List<Movie> {
        if (query.isBlank()) return emptyList()

        return repository.search(query)
            .filter { it.tomatoMeter != null }
            .sortedByDescending { it.year }
    }
}