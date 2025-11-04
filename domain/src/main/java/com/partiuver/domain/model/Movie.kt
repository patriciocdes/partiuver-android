package com.partiuver.domain.model

data class Movie(
    val id: String,
    val title: String,
    val year: Int,
    val poster: String,
    val runtime: Int,
    val rating: Double,
    val offer: String
)
