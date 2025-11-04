package com.partiuver.core.network.api

import com.partiuver.core.network.dto.JustWatchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface JustWatchApi {

    @GET("justwatch")
    suspend fun search(
        @Query("q") query: String,
        @Query("L") locale: String = "pt_BR"
    ): JustWatchResponseDto
}