package com.partiuver.app.nav

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.partiuver.feature.detail.DetailScreen
import com.partiuver.feature.search.SearchScreen

@Composable
fun AppNavGraph(nav: NavHostController, destination: String = Routes.SEARCH) {

    NavHost(navController = nav, startDestination = destination) {

        composable(Routes.SEARCH) {
            SearchScreen(
                onOpenDetail = { id -> nav.goToDetail(id) }
            )
        }

        composable(
            route = Routes.DETAIL_PATTERN,
            arguments = listOf(
                navArgument(Routes.ARG_MOVIE_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val activity = LocalContext.current as Activity

            BackHandler {
                if (!nav.popBackStack()) activity.finish()
            }

            DetailScreen(
                onBack = {
                    if (!nav.popBackStack()) {
                        activity.finish()
                    }
                }
            )
        }
    }
}