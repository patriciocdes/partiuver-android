package com.partiuver.domain.usecase

import com.partiuver.domain.repository.MovieRepository

class GetMovieDetailUseCase(private val repository: MovieRepository) {

    suspend operator fun invoke(id: String) = repository.getById(id)

}