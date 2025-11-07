package com.partiuver.feature.search

import app.cash.turbine.test
import com.partiuver.domain.common.AppError
import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.Offer
import com.partiuver.domain.repository.MovieRepository
import com.partiuver.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var dispatcher: TestDispatcher

    private class FakeRepo(
        private val block: (String) -> List<Movie>
    ) : MovieRepository {
        override suspend fun search(query: String): List<Movie> = block(query)
        override suspend fun getById(id: String): Movie? = null
    }

    private lateinit var vm: SearchViewModel

    private fun movie(id: String = "id1") = Movie(
        id = id,
        title = "Title$id",
        year = 2000,
        runtime = 120,
        tomatoMeter = 80,
        photoUrl = "https://p/$id.jpg",
        backdropUrl = "https://b/$id.jpg",
        offers = listOf(Offer(type = "RENT HD", name = "Store", url = "https://s/$id"))
    )

    @Before
    fun setup() {
        // Instala o Main de teste antes de qualquer criação que use viewModelScope/Dispatchers.Main
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        val repo = FakeRepo { q ->
            if (q == "boom") throw IllegalStateException("x")
            listOf(movie("a"), movie("b"))
        }

        val useCase = SearchMoviesUseCase(repository = repo)

        vm = SearchViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `QueryChanged atualiza state_query`() = runTest(dispatcher) {
        vm.onEvent(SearchEvent.QueryChanged("matrix"))
        assertEquals("matrix", vm.state.value.query)
    }

    @Test
    fun `Submit com query em branco limpa lista e erro`() = runTest(dispatcher) {
        vm.onEvent(SearchEvent.QueryChanged("   "))
        vm.onEvent(SearchEvent.Submit)

        assertFalse(vm.state.value.isLoading)
        assertTrue(vm.state.value.items.isEmpty())
        assertNull(vm.state.value.error)
    }

    @Test
    fun `Submit sucesso popula items e encerra loading`() = runTest(dispatcher) {
        vm.onEvent(SearchEvent.QueryChanged("ok"))
        vm.onEvent(SearchEvent.Submit)

        advanceUntilIdle() // esvazia fila das corrotinas

        val st = vm.state.value
        assertFalse(st.isLoading)
        assertNull(st.error)
        assertEquals(2, st.items.size)
        assertEquals("a", st.items.first().id)
        assertTrue(st.items.first().backdropUrl?.endsWith("/a.jpg") == true)
    }

    @Test
    fun `Submit falha seta AppError_Unknown`() = runTest(dispatcher) {
        vm.onEvent(SearchEvent.QueryChanged("boom"))
        vm.onEvent(SearchEvent.Submit)

        advanceUntilIdle()

        val st = vm.state.value
        assertFalse(st.isLoading)
        assertTrue(st.items.isEmpty())
        assertTrue(st.error is AppError.Unknown)
    }

    @Test
    fun `OpenDetail emite effect NavigateToDetail`() = runTest(dispatcher) {
        vm.effects.test {
            vm.onEvent(SearchEvent.OpenDetail("tt1"))
            assertEquals(SearchEffect.NavigateToDetail("tt1"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
