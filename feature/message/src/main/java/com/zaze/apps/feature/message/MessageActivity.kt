package com.zaze.apps.feature.message

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.core.view.WindowCompat
import com.zaze.apps.core.base.AbsActivity
import com.zaze.core.designsystem.compose.components.snackbar.MySnackbarHost
import com.zaze.core.designsystem.compose.icon.mirroringBackIcon
import com.zaze.core.designsystem.compose.theme.MyComposeTheme

/**
 * Description :
 * @author : zaze
 * @version : 2023-11-08 16:51
 */
//@Route(path = "/message/ac")
class MessageActivity : AbsActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MyApp(windowSizeClass = windowSizeClass)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyApp(
    windowSizeClass: WindowSizeClass
) {
    MyComposeTheme {
        // true（宽屏）：横向中的大多数平板电脑 和 横向中的大型展开内部显示器
        val isExpandedScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
        val coroutineScope = rememberCoroutineScope()
        //
        Scaffold(
            snackbarHost = { MySnackbarHost(hostState = remember { SnackbarHostState() }) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "TopAppBar", style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        if (!isExpandedScreen) {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = mirroringBackIcon(),
                                    contentDescription = "back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Open search */ }) {
                            Icon(
                                imageVector = Icons.Filled.Search, contentDescription = "cd_search"
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)
            Box(
                modifier = screenModifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.background(Color.Gray),
                    text = "Content",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}