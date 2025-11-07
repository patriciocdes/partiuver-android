package com.partiuver.app.nav

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val SEARCH = "search"
    const val DETAIL = "detail"
    const val ARG_MOVIE_ID = "movieId"

    fun detailRoute(movieId: String): String {
        val encoded = URLEncoder.encode(movieId, StandardCharsets.UTF_8.name())
        return "$DETAIL/$encoded"
    }

    const val DETAIL_PATTERN = "$DETAIL/{$ARG_MOVIE_ID}"
}