package com.partiuver.data.repository.mappers

import com.partiuver.core.network.dto.MovieDto
import com.partiuver.core.network.dto.OfferDto
import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.Offer

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        tomatoMeter = tomatoMeter,
        backdropUrl = backdrops?.firstOrNull(),
        photoUrl = photoUrl.firstOrNull() ?: "",
        offers = offers
            .distinctBy { it.name to it.url }
            .map { it.toDomain() }
    )
}

fun OfferDto.toDomain(): Offer {
    return Offer(
        type = type ?: "Indisponível",
        name = name ?: "Indisponível",
        url = url
    )
}