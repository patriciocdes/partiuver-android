package com.partiuver.core.network.dto

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieDtoTest {

    private val gson: Gson = GsonBuilder().create()

    @Test
    fun `construtor preenche todos os campos`() {
        val dto = MovieDto(
            id = "tt0133093",
            title = "The Matrix",
            year = 1999,
            runtime = 136,
            tomatoMeter = 88,
            backdrops = listOf("https://img/backdrop1.jpg", "https://img/backdrop2.jpg"),
            offers = listOf(
                OfferDto(type = "RENT HD", name = "Amazon Video", url = "https://prime/xyz")
            ),
            photoUrl = listOf("https://img/poster1.jpg", "https://img/poster2.jpg")
        )

        assertEquals("tt0133093", dto.id)
        assertEquals("The Matrix", dto.title)
        assertEquals(1999, dto.year)
        assertEquals(136, dto.runtime)
        assertEquals(88, dto.tomatoMeter)
        assertEquals(listOf("https://img/backdrop1.jpg", "https://img/backdrop2.jpg"), dto.backdrops)
        assertEquals(1, dto.offers.size)
        assertEquals("RENT HD", dto.offers.first().type)
        assertEquals("Amazon Video", dto.offers.first().name)
        assertEquals("https://prime/xyz", dto.offers.first().url)
        assertEquals(listOf("https://img/poster1.jpg", "https://img/poster2.jpg"), dto.photoUrl)
    }

    @Test
    fun `construtor aceita backdrops nulo e listas vazias`() {
        val dto = MovieDto(
            id = "tt0",
            title = "Empty",
            year = 2020,
            runtime = 90,
            tomatoMeter = null,
            backdrops = null,               // <- cobre caminho nulo
            offers = emptyList(),           // <- lista vazia
            photoUrl = emptyList()          // <- lista vazia
        )

        assertEquals("tt0", dto.id)
        assertEquals("Empty", dto.title)
        assertEquals(2020, dto.year)
        assertEquals(90, dto.runtime)
        assertNull(dto.tomatoMeter)
        assertNull(dto.backdrops)          // nulo deve permanecer nulo
        assertEquals(emptyList<OfferDto>(), dto.offers)
        assertEquals(emptyList<String>(), dto.photoUrl)
    }

    @Test
    fun `backdrops pode ser null e photo_url vazio`() {
        val json = """
            {
              "id":"tt1",
              "title":"Title",
              "year":2020,
              "runtime":90,
              "tomatoMeter":75,
              "backdrops": null,
              "offers": [],
              "photo_url": []
            }
        """.trimIndent()

        val movie = gson.fromJson(json, MovieDto::class.java)
        assertNull(movie.backdrops)
        assertTrue(movie.photoUrl.isEmpty())
        assertTrue(movie.offers.isEmpty())
    }

    @Test
    fun `offers podem ter name e type nulos`() {
        val json = """
            {
              "id":"tt2",
              "title":"T2",
              "year":2021,
              "runtime":100,
              "tomatoMeter":null,
              "backdrops": [],
              "offers": [
                {"type": null, "name": null, "url": "https://u"}
              ],
              "photo_url": ["https://p"]
            }
        """.trimIndent()

        val movie = gson.fromJson(json, MovieDto::class.java)
        assertEquals(1, movie.offers.size)

        val offer = movie.offers.first()
        assertNull(offer.type)
        assertNull(offer.name)
        assertEquals("https://u", offer.url)
    }
}
