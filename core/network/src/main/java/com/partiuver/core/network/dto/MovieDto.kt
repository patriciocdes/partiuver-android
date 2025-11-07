package com.partiuver.core.network.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: String,
    val title: String,
    val year: Int,
    val runtime: Int,
    val tomatoMeter: Int?,
    val backdrops: List<String>?,
    val offers: List<OfferDto>,
    @SerializedName("photo_url")
    val photoUrl: List<String>
)