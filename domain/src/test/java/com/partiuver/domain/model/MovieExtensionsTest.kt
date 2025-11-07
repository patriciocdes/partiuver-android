package com.partiuver.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MovieExtensionsTest {

    private fun base(runtime: Int, tomato: Int?) = Movie(
        id = "x",
        title = "T",
        year = 2020,
        runtime = runtime,
        tomatoMeter = tomato,
        photoUrl = "p",
        backdropUrl = null,
        offers = emptyList()
    )

    @Test
    fun `formatRuntime retorna vazio para runtime zero ou negativo`() {
        assertEquals("", base(0, 80).formatRuntime())
        assertEquals("", base(-5, 80).formatRuntime())
    }

    @Test
    fun `formatRuntime retorna apenas minutos quando menor que 60`() {
        assertEquals("45m", base(45, 80).formatRuntime())
    }

    @Test
    fun `formatRuntime retorna horas e minutos quando maior ou igual a 60`() {
        assertEquals("2h 5m", base(125, 80).formatRuntime())
        assertEquals("2h", base(120, 80).formatRuntime())
    }

    @Test
    fun `formatTomato retorna string com porcentagem quando valido`() {
        assertEquals("Tomato Meter: 93%", base(100, 93).formatTomato())
    }

    @Test
    fun `formatTomato retorna vazio quando null ou negativo`() {
        assertEquals("", base(100, null).formatTomato())
        assertEquals("", base(100, -1).formatTomato())
    }

    @Test
    fun `formatTomato retorna vazio quando null`() {
        val m = Movie("id", "T", 2020, 90, null,
            "p", null, emptyList())

        assertEquals("", m.formatTomato())
    }

    @Test
    fun `formatTomato retorna vazio quando negativo`() {
        val m = Movie("id", "T", 2020, 90, -1,
            "p", null, emptyList())

        assertEquals("", m.formatTomato())
    }

    @Test
    fun `formatTomato retorna zero porcento quando valor for 0`() {
        val m = Movie("id", "T", 2020, 90, 0,
            "p", null, emptyList())

        assertEquals("Tomato Meter: 0%", m.formatTomato())
    }

    @Test
    fun `formatTomato retorna porcentagem quando positivo`() {
        val m = Movie("id", "T", 2020, 90, 87,
            "p", null, emptyList())

        assertEquals("Tomato Meter: 87%", m.formatTomato())
    }
}
