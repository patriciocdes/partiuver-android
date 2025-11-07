package com.partiuver.data.repository.mappers

import com.partiuver.core.network.dto.MovieDto
import com.partiuver.core.network.dto.OfferDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertNull

class MovieMapperTest {

    private fun dto(
        id: String = "tt1",
        title: String = "Title",
        year: Int = 2000,
        runtime: Int = 120,
        tomato: Int? = 80,
        photos: List<String> = listOf("https://img/poster1.jpg"),
        backdrops: List<String>? = listOf("https://img/backdrop1.jpg"),
        offers: List<OfferDto> = emptyList()
    ) = MovieDto(
        id = id,
        title = title,
        year = year,
        runtime = runtime,
        tomatoMeter = tomato,
        photoUrl = photos,
        backdrops = backdrops,
        offers = offers
    )

    @Test
    fun `mapeia campos basicos, poster e backdrop (listas com itens)`() {
        val d = dto(
            photos = listOf("https://img/poster1.jpg", "https://img/poster2.jpg"),
            backdrops = listOf("https://img/backdrop1.jpg", "https://img/backdrop2.jpg"),
            offers = listOf(
                // dois offers iguais pelo par (name,url) → deve deduplicar
                OfferDto(type = "ADS SD", name = "Mercado Play", url = "https://play.com/123"),
                OfferDto(type = "ADS HD", name = "Mercado Play", url = "https://play.com/123"),
                // mesma url porém name diferente → mantém ambos
                OfferDto(type = "RENT HD", name = "Amazon Video", url = "https://prime/xyz")
            )
        )

        val m = d.toDomain()
        assertEquals("tt1", m.id)
        assertEquals("Title", m.title)
        assertEquals(2000, m.year)
        assertEquals(120, m.runtime)
        assertEquals(80, m.tomatoMeter)
        assertEquals("https://img/poster1.jpg", m.photoUrl)        // firstOrNull()
        assertEquals("https://img/backdrop1.jpg", m.backdropUrl)   // firstOrNull()

        // distinctBy { name to url } → colapsa os 2 primeiros, mantém o terceiro
        assertEquals(2, m.offers.size)
        assertTrue(m.offers.any { it.name == "Mercado Play" && it.url.endsWith("/123") })
        assertTrue(m.offers.any { it.name == "Amazon Video" && it.url.endsWith("/xyz") })
    }

    @Test
    fun `photoUrl vazio cai no default vazio`() {
        val m = dto(photos = emptyList()).toDomain()
        assertEquals("", m.photoUrl) // operador ?: ""
    }

    @Test
    fun `backdrops nulo resulta em backdropUrl nulo`() {
        val m = dto(backdrops = null).toDomain()
        assertNull(m.backdropUrl) // safe call + firstOrNull()
    }

    @Test
    fun `backdrops vazio tambem resulta em backdropUrl nulo`() {
        val m = dto(backdrops = emptyList()).toDomain()
        assertNull(m.backdropUrl) // firstOrNull() em lista vazia
    }

    @Test
    fun `OfferDto com type e name nulos vira Indisponivel`() {
        val d = dto(
            offers = listOf(OfferDto(type = null, name = null, url = "https://u"))
        )
        val o = d.toDomain().offers.first()
        assertEquals("Indisponível", o.type)
        assertEquals("Indisponível", o.name)
        assertEquals("https://u", o.url)
    }
}
