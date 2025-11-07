package com.partiuver.core.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.partiuver.core.ui.test.EmptyActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatesTest {

    @get:Rule
    val compose = createAndroidComposeRule<EmptyActivity>()

    @Test
    fun loadingState_mostraProgress_e_mensagemOpcional() {
        compose.setContent {
            LoadingState(message = "Carregando…")
        }

        compose.onNodeWithText("Carregando…").assertIsDisplayed()
    }

    @Test
    fun errorState_mostraMensagem_e_disparaRetry() {
        var retries = 0

        compose.setContent {
            ErrorState(message = "Sem rede", onRetry = { retries++ })
        }

        compose.onNodeWithText("Erro: Sem rede").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()

        assertEquals(1, retries)
    }

    @Test
    fun emptyState_mostraTitulo_e_subtitulo_quandoPresente() {
        compose.setContent {
            EmptyState(title = "Nada encontrado", subtitle = "Tente outra busca")
        }

        compose.onNodeWithText("Nada encontrado").assertIsDisplayed()
        compose.onNodeWithText("Tente outra busca").assertIsDisplayed()
    }
}
