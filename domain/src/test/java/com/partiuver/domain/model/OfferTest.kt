package com.partiuver.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OfferTest {

    @Test
    fun `ofertas iguais com mesmas propriedades sao iguais`() {
        val o1 = Offer(type = "RENT HD", name = "Apple TV", url = "https://apple.tv/123")
        val o2 = Offer(type = "RENT HD", name = "Apple TV", url = "https://apple.tv/123")
        assertEquals(o1, o2)
        assertEquals(o1.hashCode(), o2.hashCode())
    }

    @Test
    fun `ofertas com urls diferentes sao distintas`() {
        val o1 = Offer("RENT HD", "Apple TV", "https://apple.tv/123")
        val o2 = Offer("RENT HD", "Apple TV", "https://apple.tv/456")
        assertNotEquals(o1, o2)
    }

    @Test
    fun `toString contem informacoes principais`() {
        val offer = Offer("BUY", "Google Play", "https://gp.com")
        val string = offer.toString()
        assert(string.contains("BUY"))
        assert(string.contains("Google Play"))
    }

    @Test
    fun `getters de Offer sao acessados`() {
        val offer = Offer(
            type = "ADS SD",
            name = "Mercado Play",
            url  = "https://play.mercadolivre.com.br/assistir/abc"
        )

        // toca explicitamente cada getter
        assertEquals("ADS SD", offer.type)
        assertEquals("Mercado Play", offer.name)
        assertEquals("https://play.mercadolivre.com.br/assistir/abc", offer.url)
    }
}
