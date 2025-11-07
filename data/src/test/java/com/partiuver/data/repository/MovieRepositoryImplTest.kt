package com.partiuver.data.repository

import com.partiuver.core.network.api.JustWatchApi
import com.partiuver.core.network.dto.JustWatchResponseDto
import com.partiuver.core.network.dto.MovieDto
import com.partiuver.core.network.dto.OfferDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertNull

class MovieRepositoryImplTest {

    private class FakeApi(
        private val response: JustWatchResponseDto
    ) : JustWatchApi {
        override suspend fun search(
            query: String,
            locale: String
        ): JustWatchResponseDto = response
    }

    private fun movieDto(id: String) = MovieDto(
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
    fun `quando ok true retorna filmes mapeados e atualiza cache`() = runTest {
        val response = JustWatchResponseDto(
            ok = true,
            errorCode = 200,
            items = listOf(movieDto("a"), movieDto("b"))
        )

        val api = FakeApi(response)
        val repo = MovieRepositoryImpl(api)

        val out = repo.search("x")

        assertEquals(2, out.size)
        assertEquals("a", out[0].id)
        assertEquals("b", out[1].id)

        // ✅ verifica cache preenchido
        assertNotNull(repo.getById("a"))
        assertEquals("b", repo.getById("b")?.id)
    }

    @Test
    fun `quando ok false lanca IllegalStateException`() = runTest {
        val response = JustWatchResponseDto(
            ok = false,
            errorCode = 500,
            items = emptyList()
        )

        val api = FakeApi(response)
        val repo = MovieRepositoryImpl(api)

        val ex = runCatching { repo.search("qualquer") }.exceptionOrNull()
        assertTrue(ex is IllegalStateException)
        assertTrue(ex!!.message!!.contains("error_code=500"))
    }

    @Test
    fun `getById retorna null quando id nao existe no cache`() = runTest {
        val response = JustWatchResponseDto(
            ok = true,
            errorCode = 200,
            items = emptyList()
        )

        val api = FakeApi(response)
        val repo = MovieRepositoryImpl(api)

        assertNull(repo.getById("inexistente"))
    }

    @Test
    fun `getById retorna filme correto quando existe no cache`() = runTest {
        val response = JustWatchResponseDto(
            ok = true,
            errorCode = 200,
            items = listOf(movieDto("unique"))
        )

        val api = FakeApi(response)
        val repo = MovieRepositoryImpl(api)

        // primeiro popula o cache via busca
        repo.search("unique")

        // agora deve retornar o mesmo filme diretamente do cache
        val movie = repo.getById("unique")

        assertNotNull(movie)
        assertEquals("unique", movie?.id)
        assertEquals("Tunique", movie?.title)
    }
}
