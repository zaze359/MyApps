package com.zaze.apps.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

/**
 * Description :
 * @author : zaze
 * @version : 2023-01-18 01:25
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
}


// region HomeScreen
fun NavController.navigateToHome(builder: NavOptionsBuilder.() -> Unit = ::defaultOptions) {
    this.navigate(route = Screen.Home.route, builder = builder)
}

fun NavGraphBuilder.homeScreen(
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit = {},
    navController: NavHostController
) {
    composable(Screen.Home.route) {
//        HomeRouter(
//            snackbarHostState = snackbarHostState,
//            openDrawer = openDrawer,
//            navController = navController,
//            startActivity = startActivity,
//        )
    }
}
// endregion HomeScreen

fun NavController.defaultOptions(builder: NavOptionsBuilder) {
    builder.popUpTo(this.graph.findStartDestination().id) {
        saveState = true
    }
    builder.launchSingleTop = true
    builder.restoreState = true
}