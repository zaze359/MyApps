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
object SampleDestinations {
    const val HOME_ROUTE = "home"
    const val SCAFFOLD_ROUTE = "scaffold"
}


// region HomeScreen
fun NavController.navigateToHome(builder: NavOptionsBuilder.() -> Unit = ::defaultOptions) {
    this.navigate(route = SampleDestinations.HOME_ROUTE, builder = builder)
}

fun NavGraphBuilder.homeScreen(
    snackbarHostState: SnackbarHostState,
    openDrawer: () -> Unit = {},
    navController: NavHostController
) {
    composable(SampleDestinations.HOME_ROUTE) {
        HomeRouter(
            snackbarHostState = snackbarHostState,
            openDrawer = openDrawer,
            navController = navController,
            startActivity = startActivity,
        )
    }
}
// endregion HomeScreen


// region ScaffoldSample
fun NavController.navigateToScaffold(builder: NavOptionsBuilder.() -> Unit = ::defaultOptions) {
    this.navigate(route = SampleDestinations.SCAFFOLD_ROUTE, builder = builder)
}

fun NavGraphBuilder.scaffoldScreen(
    isExpandedScreen: Boolean,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    composable(SampleDestinations.SCAFFOLD_ROUTE) {
        ScaffoldSample(
            isExpandedScreen = isExpandedScreen,
            onBackPress = {
                navController.popBackStack()
            },
            snackbarHostState = snackbarHostState
        )
    }
}
// endregion Scaffold

fun NavController.defaultOptions(builder: NavOptionsBuilder) {
    builder.popUpTo(this.graph.findStartDestination().id) {
        saveState = true
    }
    builder.launchSingleTop = true
    builder.restoreState = true
}