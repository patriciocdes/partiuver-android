package com.partiuver.core.network.dto

import com.google.gson.annotations.SerializedName

data class JustWatchResponseDto(
    val ok: Boolean,
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("description") val items: List<MovieDto>
)
