package com.partiuver.feature.detail

import app.cash.turbine.test
import com.partiuver.domain.common.AppError
import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.Offer
import com.partiuver.domain.repository.MovieRepository
import com.partiuver.domain.usecase.GetMovieDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import androidx.lifecycle.SavedStateHandle

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private lateinit var savedState: SavedStateHandle

    private class FakeRepo(
        private val movie: Movie? = null,
        private val shouldThrow: Boolean = false
    ) : MovieRepository {

        override suspend fun search(query: String): List<Movie> = emptyList()

        override suspend fun getById(id: String): Movie? {
            if (shouldThrow) throw RuntimeException("boom")
            return movie
        }
    }

    private fun fakeMovie() = Movie(
        id = "tt001",
        title = "Matrix",
        year = 1999,
        runtime = 136,
        tomatoMeter = 90,
        photoUrl = "https://img.jpg",
        backdropUrl = "https://bkg.jpg",
        offers = listOf(Offer("RENT HD", "Netflix", "https://netflix.com"))
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        savedState = SavedStateHandle(mapOf("movieId" to "tt001"))
    }

    @Test
    fun `quando sucesso popula state com movie`() = scope.runTest {
        val repo = FakeRepo(movie = fakeMovie())
        val useCase = GetMovieDetailUseCase(repo)
        val vm = DetailViewModel(savedState, useCase)

        vm.onEvent(DetailEvent.Load)

        vm.state.test {
            // loading
            awaitItem()
            // loaded
            val s = awaitItem()

            assertFalse(s.isLoading)
            assertEquals("Matrix", s.movie?.title)
            assertNull(s.error)
        }
    }

    @Test
    fun `quando filme nao encontrado popula erro NotFound`() = scope.runTest {
        val repo = FakeRepo(movie = null)
        val useCase = GetMovieDetailUseCase(repo)
        val vm = DetailViewModel(savedState, useCase)

        vm.onEvent(DetailEvent.Load)

        vm.state.test {
            // loading
            awaitItem()
            // resultado
            val s = awaitItem()

            assertFalse(s.isLoading)
            assertTrue(s.error is AppError.NotFound)
        }
    }

    @Test
    fun `quando excecao popula erro Unknown`() = scope.runTest {
        val repo = FakeRepo(shouldThrow = true)
        val useCase = GetMovieDetailUseCase(repo)
        val vm = DetailViewModel(savedState, useCase)

        vm.onEvent(DetailEvent.Load)

        vm.state.test {
            // loading
            awaitItem()
            // erro
            val s = awaitItem()

            assertFalse(s.isLoading)
            assertTrue(s.error is AppError.Unknown)
        }
    }

    @Test
    fun `evento Refresh recarrega detalhe`() = scope.runTest {
        val repo = FakeRepo(movie = fakeMovie())
        val useCase = GetMovieDetailUseCase(repo)
        val vm = DetailViewModel(savedState, useCase)

        vm.onEvent(DetailEvent.Refresh)

        vm.state.test {
            // loading
            awaitItem()
            // loaded
            val s = awaitItem()

            assertEquals("Matrix", s.movie?.title)
        }
    }
}
