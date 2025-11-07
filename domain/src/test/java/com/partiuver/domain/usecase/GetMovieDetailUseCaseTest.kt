package com.partiuver.domain.usecase

import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.Offer
import com.partiuver.domain.repository.MovieRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetMovieDetailUseCaseTest {

    private class FakeRepo(
        private val movies: List<Movie>
    ) : MovieRepository {
        override suspend fun search(query: String) = movies
        override suspend fun getById(id: String) = movies.find { it.id == id }
    }

    private fun movie(id: String) = Movie(
        id = id,
        title = "Title $id",
        year = 2000,
        runtime = 120,
        tomatoMeter = 88,
        photoUrl = "p",
        backdropUrl = null,
        offers = listOf(Offer("TYPE","NAME","URL"))
    )

    @Test
    fun `retorna filme quando id existe`() = runTest {
        val repo = FakeRepo(listOf(movie("tt1"), movie("tt2")))
        val use = GetMovieDetailUseCase(repo) // delega para repository.getById(id) :contentReference[oaicite:6]{index=6}

        val result = use.invoke("tt2")

        assertEquals("tt2", result?.id)
    }

    @Test
    fun `retorna null quando id nao existe`() = runTest {
        val repo = FakeRepo(emptyList())
        val use = GetMovieDetailUseCase(repo)

        val result = use.invoke("missing")

        assertNull(result)
    }
}
