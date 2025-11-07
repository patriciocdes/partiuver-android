package com.partiuver.feature.detail

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.partiuver.core.ui.components.LoadingState
import com.partiuver.domain.model.Movie
import com.partiuver.domain.model.Offer
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private fun fakeMovie() = Movie(
        id = "tt001",
        title = "Matrix",
        year = 1999,
        runtime = 136,
        tomatoMeter = 90,
        photoUrl = "https://img.jpg",
        backdropUrl = "https://bkg.jpg",
        offers = listOf(
            Offer(type = "RENT HD", name = "Netflix", url = "https://netflix.com")
        )
    )

    @Test
    fun detailContent_mostraTituloAnoEOfertas() {
        val movie = fakeMovie()

        compose.setContent {
            DetailContent(movie = movie)
        }

        // Título (Matrix) + ano no formato "Matrix (1999)"
        compose.onNodeWithText("${movie.title} (${movie.year})")
            .assertIsDisplayed()

        // Exibe pelo menos uma oferta (nome e tipo)
        compose.onNodeWithText("Netflix").assertIsDisplayed()
        compose.onNodeWithText("RENT HD").assertIsDisplayed()
    }

    @Test
    fun detailContent_quandoSemOfertas_exibeMensagemDeVazio() {
        val movie = fakeMovie().copy(offers = emptyList())

        compose.setContent {
            DetailContent(movie = movie)
        }

        // Mensagem de vazio definida no composable
        compose.onNodeWithText("Nenhuma oferta disponível.")
            .assertIsDisplayed()
    }

    @Test
    fun offerRow_mostraNomeTipo_e_temAcaoDeAbrir() {
        compose.setContent {
            OfferRow(
                name = "Netflix",
                type = "RENT HD",
                onOpen = { /* no-op */ }
            )
        }

        // Verifica que o nome e o tipo aparecem na tela
        compose.onNodeWithText("Netflix").assertIsDisplayed()
        compose.onNodeWithText("RENT HD").assertIsDisplayed()

        // Verifica que o botão "Conferir" está visível e clicável
        compose.onNodeWithText("Conferir")
            .assertExists()
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun errorBox_mostraMensagem_e_tentarNovamente_disparaAcao() {
        var retried = false

        compose.setContent {
            ErrorBox(
                message = "Tente novamente mais tarde",
                onRetry = { retried = true }
            )
        }

        compose.onNodeWithText("Tente novamente mais tarde").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed().performClick()

        assert(retried)
    }

    @Test
    fun loadingState_mostraIndicador_semTestTag() {
        compose.setContent {
            LoadingState(message = "Carregando detalhes...")
        }

        // Usa semantics do ProgressIndicator (Circular → Indeterminate)
        compose.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate),
            useUnmergedTree = true
        ).assertIsDisplayed()

        compose.onNodeWithText("Carregando detalhes...").assertIsDisplayed()
    }
}
