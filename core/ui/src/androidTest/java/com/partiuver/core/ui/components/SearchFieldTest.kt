package com.partiuver.core.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.partiuver.core.ui.test.EmptyActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFieldTest {

    @get:Rule
    val compose = createAndroidComposeRule<EmptyActivity>()

    @Test
    fun digitarTextoEAcionarBuscaPeloIme() {
        var submitted = 0

        compose.setContent {
            val state = remember { mutableStateOf("") }
            SearchField(
                modifier = Modifier.testTag("search"),
                query = state.value,
                onQueryChange = { state.value = it },
                onSubmit = { submitted++ },
                label = "Buscar"
            )
        }

        // Label visível
        compose.onNodeWithText("Buscar").assertExists()

        val field = compose.onNodeWithTag("search")
        field.performTextInput("matrix")

        // Verifica o texto digitado no nó filho (árvore NÃO mesclada)
        compose.onNode(hasTextExactly("matrix"), useUnmergedTree = true).assertExists()

        // Dispara IME action
        field.performImeAction()
        assertEquals(1, submitted)
    }
}
