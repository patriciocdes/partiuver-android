package com.partiuver.core.network.dto

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.*
import org.junit.Test

class JustWatchResponseDtoTest {

    private val gson: Gson = GsonBuilder().create()

    private fun movie(id: String) = MovieDto(
        id = id,
        title = "T$id",
        year = 2000,
        runtime = 100,
        tomatoMeter = 70,
        backdrops = listOf("https://b/$id.jpg"),
        offers = listOf(OfferDto(type = "RENT", name = "Store", url = "https://store/$id")),
        photoUrl = listOf("https://p/$id.jpg")
    )

    @Test
    fun `mapeia description -items- e error_code corretamente`() {
        val json = """
            {
              "ok": true,
              "error_code": 200,
              "description": []
            }
        """.trimIndent()

        val dto = gson.fromJson(json, JustWatchResponseDto::class.java)
        assertTrue(dto.ok)
        assertEquals(200, dto.errorCode)
        assertTrue(dto.items.isEmpty())
    }

    @Test
    fun `construtor com items preenche todos os campos`() {
        val items = listOf(movie("a"), movie("b"))
        val dto = JustWatchResponseDto(
            ok = true,
            errorCode = 200,
            items = items
        )

        assertEquals(true, dto.ok)
        assertEquals(200, dto.errorCode)
        assertEquals(items, dto.items)
        assertEquals(2, dto.items.size)
        assertEquals("a", dto.items[0].id)
        assertEquals("b", dto.items[1].id)
    }

    @Test
    fun `construtor com lista vazia`() {
        val dto = JustWatchResponseDto(
            ok = false,
            errorCode = 500,
            items = emptyList()
        )

        assertEquals(false, dto.ok)
        assertEquals(500, dto.errorCode)
        assertEquals(0, dto.items.size)
    }
}
