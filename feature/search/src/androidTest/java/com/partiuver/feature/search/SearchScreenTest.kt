package com.partiuver.feature.search

import android.annotation.SuppressLint
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.partiuver.core.ui.test.EmptyActivity
import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.Offer
import com.partiuver.domain.repository.MovieRepository
import com.partiuver.domain.usecase.SearchMoviesUseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val compose = createAndroidComposeRule<EmptyActivity>()

    private fun movie(id: String) = Movie(
        id = id,
        title = "Matrix $id",
        year = 1999,
        runtime = 136,
        tomatoMeter = 88,
        photoUrl = "https://p/$id.jpg",
        backdropUrl = "https://b/$id.jpg",
        offers = listOf(Offer(type = "RENT HD", name = "Store", url = "https://s/$id"))
    )

    private inner class RepoOk : MovieRepository {
        override suspend fun search(query: String): List<Movie> = listOf(movie("a"), movie("b"))
        override suspend fun getById(id: String): Movie? = movie(id)
    }

    private class RepoFail : MovieRepository {
        override suspend fun search(query: String): List<Movie> = error("boom")
        override suspend fun getById(id: String): Movie? = null
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun botao_buscar_habilita_quando_query_nao_vazia_e_click_dispara_submit() {
        val useCase = SearchMoviesUseCase(RepoOk())

        compose.setContent {
            SearchScreen(
                onOpenDetail = {},
                searchViewModel = SearchViewModel(useCase)
            )
        }

        val btnText = compose.activity.getString(R.string.search)
        val fieldLabel = compose.activity.getString(R.string.search_movie)

        // começa desabilitado
        compose.onNodeWithText(btnText).assertIsNotEnabled()

        // digita
        compose.onNodeWithText(fieldLabel).performTextInput("matrix")

        // habilita e clica
        compose.onNodeWithText(btnText).assertIsEnabled().performClick()

        // resultados na lista (usa título formatado pela UI)
        compose.onNodeWithText("Matrix a (1999)").assertExists()
        compose.onNodeWithText("Matrix b (1999)").assertExists()
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun click_no_card_dispara_callback_de_navegacao() {
        val useCase = SearchMoviesUseCase(RepoOk())
        var opened: String? = null

        compose.setContent {
            SearchScreen(
                onOpenDetail = { opened = it },
                searchViewModel = SearchViewModel(useCase)
            )
        }

        val fieldLabel = compose.activity.getString(R.string.search_movie)
        val btnText = compose.activity.getString(R.string.search)

        // Executa uma busca
        compose.onNodeWithText(fieldLabel).performTextInput("matrix")
        compose.onNodeWithText(btnText).performClick()

        // Clica no primeiro item
        compose.onNodeWithText("Matrix a (1999)").performClick()

        // Callback foi chamado com o id correto
        assertEquals("a", opened)
    }

    @SuppressLint("ViewModelConstructorInComposable")
    @Test
    fun erro_no_usecase_exibe_snackbar() {
        val useCase = SearchMoviesUseCase(RepoFail())

        compose.setContent {
            SearchScreen(
                onOpenDetail = {},
                searchViewModel = SearchViewModel(useCase)
            )
        }

        val fieldLabel = compose.activity.getString(R.string.search_movie)
        val btnText   = compose.activity.getString(R.string.search)
        val msgErro   = compose.activity.getString(com.partiuver.core.ui.R.string.error_unknown)
        // msgErro deve ser exatamente "Tente novamente mais tarde" (o texto da snackbar)

        compose.onNodeWithText(fieldLabel).performTextInput("boom")
        compose.onNodeWithText(btnText).performClick()

        // Procure pelo texto EXATO da snackbar (sem substring)
        // Caso ainda haja mais de um nó com o mesmo texto, valide a contagem e pegue o primeiro.
        compose.onAllNodes(hasTextExactly(msgErro))
            .assertCountEquals(1)
            .onFirst()
            .assertExists()
    }
}
