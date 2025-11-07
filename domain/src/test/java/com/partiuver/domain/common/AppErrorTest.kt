package com.partiuver.domain.common

import org.junit.Assert.*
import org.junit.Test

class AppErrorTest {

    @Test
    fun `NotFound deve ser igual a si mesmo e diferente de Unknown`() {
        val a = AppError.NotFound
        val b = AppError.NotFound
        val c = AppError.Unknown()

        assertEquals(a, b)         // mesmo objeto singleton
        assertNotEquals(a, c)    // subclasses diferentes
        assertEquals(a.hashCode(), b.hashCode())
        assertTrue(a.toString().contains("NotFound"))
    }

    @Test
    fun `Unknown com a mesma causa deve ser igual`() {
        val causa = IllegalArgumentException("boom")
        val u1 = AppError.Unknown(causa)
        val u2 = AppError.Unknown(causa) // mesma referência da exceção

        assertEquals(u1, u2)
        assertEquals(u1.hashCode(), u2.hashCode())
        assertTrue(u1.toString().contains("Unknown"))
    }

    @Test
    fun `Unknown com causas diferentes não deve ser igual`() {
        val u1 = AppError.Unknown(IllegalStateException("x"))
        val u2 = AppError.Unknown(IllegalStateException("x")) // instâncias distintas

        assertNotEquals(u1, u2)
    }

    @Test
    fun `Unknown pode ter causa nula`() {
        val u = AppError.Unknown(null)

        assertNull(u.cause)
        assertTrue(u.toString().contains("Unknown"))
    }
}
