package com.partiuver.app.nav

import androidx.navigation.NavController

fun NavController.goToDetail(movieId: String) {
    navigate(Routes.detailRoute(movieId))
}