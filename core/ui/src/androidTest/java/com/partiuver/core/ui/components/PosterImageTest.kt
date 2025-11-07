package com.partiuver.core.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.partiuver.core.ui.test.EmptyActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PosterImageTest {

    @get:Rule
    val compose = createAndroidComposeRule<EmptyActivity>()

    @Test
    fun posterImage_quandoUrlVazia_renderizaBoxPlaceholder() {
        compose.setContent {
            PosterImage(
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .testTag("poster"),
                url = ""
            )
        }
        compose.onNodeWithTag("poster").assertExists()
    }

    @Test
    fun posterImage_quandoUrlValida_composeNaoCrasha() {
        // Aqui o objetivo é apenas exercitar o ramo com AsyncImage (coverage)
        compose.setContent {
            PosterImage(
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .testTag("poster"),
                url = "https://example.com/img.jpg"
            )
        }
        compose.onNodeWithTag("poster").assertExists()
    }
}
