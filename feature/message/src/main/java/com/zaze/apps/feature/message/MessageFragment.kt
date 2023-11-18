package com.zaze.apps.feature.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zaze.apps.core.base.AbsFragment
import com.zaze.apps.feature.message.navigation.messageRoute
import com.zaze.apps.feature.message.navigation.messageScreen
import com.zaze.core.designsystem.compose.theme.MyComposeTheme

/**
 * Description :
 * @author : zaze
 * @version : 2023-11-13 20:18
 */
//@Route(path = "/message/main")
class MessageFragment : AbsFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        return ComposeView(requireContext()).apply {
            // 设置重组策略，和 fragment.view 关联
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MessageMain()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun MessageMain() {
    MyComposeTheme {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        NavHost(
            navController = navController,
            startDestination = messageRoute
        ) {
            messageScreen(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }
        ////////////
//        val layoutDirection = LocalLayoutDirection.current
//        val density = LocalDensity.current
//        Scaffold(
//            modifier = Modifier,
//            snackbarHost = { MySnackbarHost(hostState = remember { SnackbarHostState() }) },
//            // 去除 WindowInsets 底部的边距
//            contentWindowInsets = WindowInsets(
//                ScaffoldDefaults.contentWindowInsets.getLeft(
//                    density,
//                    layoutDirection
//                ),
//                ScaffoldDefaults.contentWindowInsets.getTop(
//                    density
//                ),
//                ScaffoldDefaults.contentWindowInsets.getRight(
//                    density,
//                    layoutDirection
//                ),
//                0
//            ),
//            topBar = {
//                CenterAlignedTopAppBar(
//                    title = {
//                        Text(
//                            text = "TopAppBar",
//                            style = MaterialTheme.typography.titleLarge
//                        )
//                    },
//                    navigationIcon = {
//                        IconButton(onClick = {}) {
//                            Icon(
//                                imageVector = mirroringBackIcon(),
//                                contentDescription = "back",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    },
//                    actions = {
//                        IconButton(onClick = { /* TODO: Open search */ }) {
//                            Icon(
//                                imageVector = Icons.Filled.Search,
//                                contentDescription = "cd_search"
//                            )
//                        }
//                    },
//                )
//            },
//        ) { innerPadding ->
//            val screenModifier = Modifier
//                .padding(innerPadding)
//                .background(color = Color.Black)
//            Box(
//                modifier = screenModifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//
//            }
//        }
    }
}