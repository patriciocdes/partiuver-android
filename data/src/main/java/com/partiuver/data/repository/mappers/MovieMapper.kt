package com.partiuver.data.repository.mappers

import com.partiuver.core.network.dto.MovieDto
import com.partiuver.domain.model.Movie

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = imdbId,
        title = title,
        year = year,
        poster = poster,
        runtime = runtime,
        rating = rating,
        offer = offer
    )
}