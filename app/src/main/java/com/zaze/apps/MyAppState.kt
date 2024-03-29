package com.zaze.apps

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.trace
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.zaze.apps.navigation.navigateToHome
import com.zaze.apps.navigation.TopLevelDestination
import com.zaze.core.designsystem.compose.components.snackbar.SnackbarManager
import com.zaze.core.designsystem.compose.components.snackbar.toTextString
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2023-01-14 00:32
 */
@Composable
fun rememberMyAppState(
    windowSizeClass: WindowSizeClass,
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController()

) = remember {
    MyAppState(
        snackbarHostState = snackbarHostState,
        snackbarManager = snackbarManager,
        resources = resources,
        coroutineScope = coroutineScope,
        navController = navController,
        windowSizeClass = windowSizeClass
    )
}

class MyAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    val windowSizeClass: WindowSizeClass,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()
    init {
        // 每一个使用了 这个 snackbarHostState 地方都会显示。
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                if (currentMessages.isNotEmpty()) {
                    val message = currentMessages[0]
                    val result = snackbarHostState.showSnackbar(message.toTextString(resources))
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            ZLog.d(ZTag.TAG_DEBUG, "ActionPerformed")
                        }
                        SnackbarResult.Dismissed -> {
                            ZLog.d(ZTag.TAG_DEBUG, "Dismissed")
                        }
                    }
                    snackbarManager.setMessageShown(message.id)
                }
            }
        }
    }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // 仅保存顶部导航的状态
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
//            when (topLevelDestination) {
//                TopLevelDestination.OVERVIEW -> navController.navigateToHome(topLevelNavOptions)
//                TopLevelDestination.APPS -> navController.navigateToScaffold(topLevelNavOptions)
//                TopLevelDestination.MESSAGE -> navController.navigateToCommunication(
//                    topLevelNavOptions
//                )
//            }
        }
    }


//    fun NavController.defaultOptions(builder: NavOptionsBuilder) {
//        builder.popUpTo(this.graph.findStartDestination().id) {
//            saveState = true
//        }
//        builder.launchSingleTop = true
//        builder.restoreState = true
//    }
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
