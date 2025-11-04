package com.partiuver.core.network.dto

data class MovieDto(
    val imdbId: String,
    val title: String,
    val year: Int,
    val poster: String,
    val runtime: Int,
    val rating: Double,
    val offer: String
)