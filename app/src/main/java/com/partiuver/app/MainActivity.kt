package com.partiuver.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.partiuver.app.nav.AppNavGraph
import com.partiuver.app.nav.Routes
import com.partiuver.core.ui.theme.PartiuVerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var initialStartRoute: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialStartRoute = intent?.data?.lastPathSegment?.let { Routes.detailRoute(it) }

        enableEdgeToEdge()

        setContent {
            PartiuVerTheme {
                val nav = rememberNavController()
                AppNavGraph(nav, initialStartRoute ?: Routes.SEARCH)
            }
        }
    }
}