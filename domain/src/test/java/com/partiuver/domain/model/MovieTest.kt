package com.partiuver.domain.model

import org.junit.Assert.*
import org.junit.Test

class MovieTest {

    private fun movie(runtime: Int, tomato: Int?) = Movie(
        id = "m1",
        title = "Matrix",
        year = 1999,
        runtime = runtime,
        tomatoMeter = tomato,
        photoUrl = "https://img",
        backdropUrl = null,
        offers = emptyList()
    )

    @Test
    fun `formatRuntime retorna vazio se runtime menor ou igual a zero`() {
        assertEquals("", movie(0, 80).formatRuntime())
        assertEquals("", movie(-10, 80).formatRuntime())
    }

    @Test
    fun `formatRuntime retorna minutos quando menor que 60`() {
        assertEquals("45m", movie(45, 90).formatRuntime())
    }

    @Test
    fun `formatRuntime retorna horas completas ou horas e minutos`() {
        assertEquals("2h", movie(120, 90).formatRuntime())
        assertEquals("2h 5m", movie(125, 90).formatRuntime())
    }

    @Test
    fun `formatTomato retorna string correta quando valido`() {
        assertEquals("Tomato Meter: 87%", movie(100, 87).formatTomato())
    }

    @Test
    fun `formatTomato retorna vazio quando null ou negativo`() {
        assertEquals("", movie(100, null).formatTomato())
        assertEquals("", movie(100, -1).formatTomato())
    }

    @Test
    fun `equals e hashCode funcionam corretamente`() {
        val m1 = movie(120, 80)
        val m2 = movie(120, 80)
        assertEquals(m1, m2)
        assertEquals(m1.hashCode(), m2.hashCode())
    }

    @Test
    fun `getters de Movie sao acessados`() {
        val offers = listOf(
            Offer(type = "RENT HD", name = "Amazon Video", url = "https://prime/123")
        )

        val movie = Movie(
            id = "tt001",
            title = "Sexta-Feira 13",
            year = 1980,
            runtime = 95,
            tomatoMeter = 78,
            photoUrl = "https://img/poster.jpg",
            backdropUrl = "https://img/backdrop.jpg",
            offers = offers
        )

        // toca explicitamente cada getter que faltou na cobertura
        assertEquals("Sexta-Feira 13", movie.title)
        assertEquals("https://img/poster.jpg", movie.photoUrl)
        assertEquals("https://img/backdrop.jpg", movie.backdropUrl)
        assertEquals(offers, movie.offers)

        // (opcional) tocar id/year/runtime/tomato també m
        assertEquals("tt001", movie.id)
        assertEquals(1980, movie.year)
        assertEquals(95, movie.runtime)
        assertEquals(78, movie.tomatoMeter)
    }
}
