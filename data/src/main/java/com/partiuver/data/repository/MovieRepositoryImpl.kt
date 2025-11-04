package com.partiuver.data.repository

import com.partiuver.core.network.api.JustWatchApi
import com.partiuver.data.repository.mappers.toDomain
import com.partiuver.domain.model.Movie
import com.partiuver.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: JustWatchApi
) : MovieRepository {

    private val cache = mutableMapOf<String, Movie>()

    override suspend fun search(query: String): List<Movie> {
        val response = api.search(query)

        if (!response.ok) {
            val code = response.errorCode
            throw IllegalStateException("Falha na busca (error_code=$code)")
        }

        val movies = response.items
            .map { it.toDomain() }

        movies.forEach { cache[it.id] = it }

        return movies
    }

    override suspend fun getById(id: String) = cache[id]
}