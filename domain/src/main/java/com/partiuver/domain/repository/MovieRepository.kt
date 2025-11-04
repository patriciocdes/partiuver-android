package com.partiuver.domain.repository

import com.partiuver.domain.model.Movie

interface MovieRepository {
    suspend fun search(query: String): List<Movie>
    suspend fun getById(id: String): Movie?
}