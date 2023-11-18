package com.zaze.apps.feature.message

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.zaze.apps.feature.message.data.ChatMessage
import com.zaze.apps.feature.message.data.ChatMode
import com.zaze.apps.feature.message.data.ChatRoom
import com.zaze.core.designsystem.compose.icon.mirroringBackIcon
import com.zaze.core.designsystem.compose.components.snackbar.MySnackbarHost
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.launch
import java.net.URLDecoder


typealias JoinRoomListener = (ChatRoom) -> Unit

@Composable
internal fun MessageRoute(
    viewModel: MessageViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onBackPress: () -> Unit,
    joinRoom: (ChatRoom) -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current // 当前这个Composable的生命周期
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    MessageScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackPress = onBackPress,
        onSwitchMode = viewModel::onSwitchMode,
        onMessageSent = viewModel::sendMessage,
        onPictureSend = viewModel::sendPicture,
        onFileSend = viewModel::onFileSend,
        openCamera = {
            ZLog.i(ZTag.TAG, "openCamera")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("openCamera")
            }
        },
        openPhoto = {
            ZLog.i(ZTag.TAG, "openPhoto")
            coroutineScope.launch {
                snackbarHostState.showSnackbar("openPhoto")
            }
        },
        joinRoom = joinRoom
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun MessageScreen(
    uiState: MessageUiState,
    snackbarHostState: SnackbarHostState,
    onBackPress: () -> Unit,
    onMessageSent: (String) -> Unit,
    onPictureSend: (String) -> Unit,
    onFileSend: (List<String>) -> Unit,
    onSwitchMode: (ChatMode) -> Unit,
    openCamera: () -> Unit,
    openPhoto: () -> Unit,
    joinRoom: JoinRoomListener,
) {
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // 去除 WindowInsets 底部的边距
        contentWindowInsets = WindowInsets(
            ScaffoldDefaults.contentWindowInsets.getLeft(
                density,
                layoutDirection
            ),
            ScaffoldDefaults.contentWindowInsets.getTop(
                density
            ),
            ScaffoldDefaults.contentWindowInsets.getRight(
                density,
                layoutDirection
            ),
            0
        ),
        snackbarHost = { MySnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.message),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = mirroringBackIcon(),
                            contentDescription = "back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
            )
        }) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        val scrollState = rememberLazyListState()
        ChatRoomList(
            scrollState = scrollState,
            modifier = screenModifier,
            rooms = uiState.chatRooms,
            joinRoom = joinRoom
        )
    }
}

/**
 * 聊天室列表
 */
@Composable
private fun ChatRoomList(
    rooms: List<ChatRoom>,
    scrollState: LazyListState,
    modifier: Modifier,
    joinRoom: JoinRoomListener
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
//        reverseLayout = true,
        state = scrollState,
//        contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 90.dp))
//            .asPaddingValues(),
    ) {
        items(items = rooms,
            key = {
                it.id
            }) {
            ChatRoomItem(room = it, joinRoom = joinRoom)
        }
    }
}

@Composable
private fun ChatRoomItem(room: ChatRoom, joinRoom: JoinRoomListener) {
    SideEffect {
        Log.d("重组监听", "重组一次${room.id}")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable( // 去除水波纹
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                joinRoom(room)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = room.name,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${room.members.size}人正在讨论",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        ElevatedButton(modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            onClick = {
                joinRoom(room)
            }
        ) {
            Text(text = "加入")
        }
    }

}

@Composable
private fun Avatar(
    modifier: Modifier,
    message: ChatMessage,
    isUserMe: Boolean,
    onAvatarClick: (Long) -> Unit
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    val painter = message.authorImage?.let { url ->
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            onState = {
                ZLog.d(ZTag.TAG, "Avatar onState ${it}: $message")
            }
        )
    } ?: painterResource(id = R.drawable.bg_square)
    Box {
        Image(
            modifier = modifier
                .border(1.5.dp, borderColor, CircleShape)
                .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .clip(CircleShape)
                .clickable {
                    onAvatarClick(message.userId)
                },
//            colorFilter = ColorFilter.tint(Color.Green, blendMode = BlendMode.Darken),
            painter = painter,
            contentScale = ContentScale.Fit,
            contentDescription = message.author
        )
        Text(modifier = Modifier.align(Alignment.Center), text = "${message.author[0]}")
    }
}

@Composable
private fun FileItem(
    modifier: Modifier,
    url: String?,
    mimeType: String?
) {
    if (mimeType?.startsWith("image/") == true) {
        ImageItem(
            modifier = modifier,
            url = url,
        )
    } else {
        Text(
            modifier = modifier,
            text = "${mimeType}: ${URLDecoder.decode(url)}",
            style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        )
    }
}

@Composable
private fun ImageItem(
    modifier: Modifier,
    url: String?,
) {
    url?.let {
//        Image(
//            painter = painterResource(R.drawable.place_holder),
////            painter = rememberAsyncImagePainter(
////                model = ImageRequest.Builder(LocalContext.current)
////                    .data(url)
////                    .size(1920, 1080)
////                    .crossfade(true)
////                    .build(),
////                onState = {
////                    ZLog.d(ZTag.TAG, "onState: $it")
////                }
////            ),
//            contentScale = ContentScale.Fit,
//            modifier = Modifier.size(160.dp),
//            contentDescription = ""
//        )

        SubcomposeAsyncImage(
            modifier = modifier,
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
//                .size(1920, 1080)
                .size(480, 270)
                .crossfade(true)
                .build(),
            loading = {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            },
            contentScale = ContentScale.Crop,
            error = {
                painterResource(id = R.drawable.ic_place_holder)
            },
            contentDescription = stringResource(id = R.string.attached_image),
            onSuccess = {
                ZLog.d(ZTag.TAG_DEBUG, "AsyncImage onSuccess: $url")
            },
            onError = {
                ZLog.w(ZTag.TAG_DEBUG, "AsyncImage onError: $url")
            },
            onLoading = {
                ZLog.d(ZTag.TAG_DEBUG, "AsyncImage onLoading: $url")
            }
        )

        // ------------------

//        AsyncImage(
//            placeholder = painterResource(R.drawable.place_holder),
//            modifier = modifier,
//            contentScale = ContentScale.Crop,
//            error = painterResource(id = R.drawable.place_holder),
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(url)
//                .crossfade(true)
//                .build(),
//            contentDescription = stringResource(id = R.string.attached_image),
//            onSuccess = {
//                ZLog.d(ZTag.TAG_DEBUG, "AsyncImage onSuccess")
//            },
//            onError = {
//                ZLog.d(ZTag.TAG_DEBUG, "AsyncImage onError")
//            },
//            onLoading = {
//                ZLog.d(ZTag.TAG_DEBUG, "AsyncImage onLoading")
//            }
//        )
    }
}