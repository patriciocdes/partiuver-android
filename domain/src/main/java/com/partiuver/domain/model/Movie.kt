package com.partiuver.domain.model

data class Movie(
    val id: String,
    val title: String,
    val year: Int,
    val runtime: Int,
    val tomatoMeter: Int?,
    val photoUrl: String,
    val backdropUrl: String?,
    val offers: List<Offer>
)

fun Movie.formatRuntime(): String =
    when {
        runtime <= 0 -> ""
        runtime < 60 -> "${runtime}m"
        else -> {
            val h = runtime / 60
            val m = runtime % 60
            if (m == 0) "${h}h" else "${h}h ${m}m"
        }
    }

fun Movie.formatTomato(): String {
    val t = tomatoMeter ?: return ""
    return if (t >= 0) "Tomato Meter: ${t}%" else ""
}