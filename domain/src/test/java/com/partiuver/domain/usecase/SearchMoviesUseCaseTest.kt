package com.partiuver.domain.usecase

import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.Offer
import com.partiuver.domain.repository.MovieRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchMoviesUseCaseTest {

    private class FakeRepo(
        private val movies: List<Movie>
    ) : MovieRepository {
        override suspend fun search(query: String): List<Movie> = movies
        override suspend fun getById(id: String): Movie? = movies.find { it.id == id }
    }

    private fun movie(
        id: String,
        year: Int,
        tomato: Int? = 90
    ) = Movie(
        id = id,
        title = "T$id",
        year = year,
        runtime = 100,
        tomatoMeter = tomato,
        photoUrl = "p",
        backdropUrl = null,
        offers = listOf(Offer("TYPE","NAME","URL"))
    )

    @Test
    fun `quando query em branco retorna lista vazia`() = runTest {
        val repo = FakeRepo(listOf(movie("1", 2020)))
        val use = SearchMoviesUseCase(repo)

        val out = use.invoke("")
        assertTrue(out.isEmpty())
    }

    @Test
    fun `filtra filmes com tomatoMeter null`() = runTest {
        val repo = FakeRepo(listOf(
            movie("1", 2020, tomato = null),
            movie("2", 2021, tomato = 80)
        ))

        val use = SearchMoviesUseCase(repo)

        val out = use.invoke("matrix")
        assertEquals(1, out.size)
        assertEquals("2", out.first().id)
    }

    @Test
    fun `ordena por ano desc`() = runTest {
        val repo = FakeRepo(listOf(
            movie("a", 1999, 70),
            movie("b", 2003, 65),
            movie("c", 2010, 95)
        ))

        val use = SearchMoviesUseCase(repo)

        val out = use.invoke("x")
        assertEquals(listOf("c","b","a"), out.map { it.id })
    }
}
