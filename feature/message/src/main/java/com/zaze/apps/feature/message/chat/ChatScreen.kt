package com.zaze.apps.feature.message.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
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
import com.zaze.apps.feature.message.R
import com.zaze.apps.feature.message.data.ChatMessage
import com.zaze.apps.feature.message.data.ChatMode
import com.zaze.apps.feature.message.widget.MessageInput
import com.zaze.core.designsystem.compose.components.snackbar.MySnackbarHost
import com.zaze.core.designsystem.compose.icon.mirroringBackIcon
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.launch
import java.net.URLDecoder

/**
 * Description :
 * @author : zaze
 * @version : 2023-11-16 19:24
 */
@Composable
internal fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onBackPress: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current // 当前这个Composable的生命周期
) {
    // 连接 messengerService
//    val messengerServiceConnection = remember {
//        mutableStateOf(object : ServiceConnection {
//            override fun onServiceDisconnected(name: ComponentName?) {
//                viewModel.serviceMessenger = null
//            }
//
//            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                viewModel.serviceMessenger = Messenger(service)
//            }
//        })
//    }

    // 连接 remoteService
//    val remoteServiceServiceConnection = remember {
//        mutableStateOf(object : ServiceConnection {
//            override fun onServiceDisconnected(name: ComponentName?) {
//                viewModel.onRemoteServiceDisconnected()
//            }
//
//            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                viewModel.onRemoteServiceConnected(service)
//            }
//        })
//    }
//    val context = LocalContext.current
//    DisposableEffect(key1 = lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            ZLog.i("IpcScreen", "event: $event")
//            when (event) {
//                Lifecycle.Event.ON_CREATE -> {
//                    // TODO connect service
//                }
//
//                else -> {
//                }
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose { // 解绑
//            // TODO 销毁时 断开服务
//            ZLog.i("IpcScreen", "解绑")
//        }
//    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    println("viewModel roomId: ${uiState.roomId}")
    ChatScreen(
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
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun ChatScreen(
    uiState: ChatUiState,
    snackbarHostState: SnackbarHostState,
    onBackPress: () -> Unit,
    onMessageSent: (String) -> Unit,
    onPictureSend: (String) -> Unit,
    onFileSend: (List<String>) -> Unit,
    onSwitchMode: (ChatMode) -> Unit,
    openCamera: () -> Unit,
    openPhoto: () -> Unit,
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
        IpcConversation(
            uiState = uiState,
            modifier = screenModifier,
            onMessageSent = onMessageSent,
            onPictureSend = onPictureSend,
            onFileSend = onFileSend,
            onSwitchMode = onSwitchMode,
            openCamera = {
            },
            openPhoto = {
                openPhoto()
            },
            snackbarHostState = snackbarHostState,
        )
    }
}

@Composable
private fun IpcConversation(
    uiState: ChatUiState,
    onMessageSent: (String) -> Unit,
    onPictureSend: (String) -> Unit,
    onFileSend: (List<String>) -> Unit,
    onSwitchMode: (ChatMode) -> Unit,
    snackbarHostState: SnackbarHostState,
    openCamera: () -> Unit,
    openPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConversationMessages(
            me = uiState.me,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            messages = uiState.messages,
            scrollState = scrollState
        )
        // 选择IPC通信方式
        IpcModeSelector(onSwitchMode = onSwitchMode, currentMode = uiState.chatMode)
        // 输入、发送消息
        MessageInput(
            onMessageSent = onMessageSent,
            onPictureSend = onPictureSend,
            onFileSend = onFileSend,
            resetScroll = {
                scope.launch {
                    scrollState.scrollToItem(0)
                }
            },
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding() // 将输入面板，移到导航栏和 IME 之上
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IpcModeSelector(
    currentMode: ChatMode,
    onSwitchMode: (ChatMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        disabledContainerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
    )
    LazyHorizontalStaggeredGrid(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp),
        rows = StaggeredGridCells.Fixed(2),
        // Grid的内边距
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        // item间的垂直间距
        verticalArrangement = Arrangement.spacedBy(4.dp),
        // item间的水平间距
        horizontalItemSpacing = 8.dp
    ) {
        items(ChatMode.values()) {
            Button(
                enabled = currentMode != it,
                onClick = {
                    onSwitchMode(it)
                },
                colors = buttonColors,
                border = if (currentMode != it) BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ) else null,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = it.name,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
//    val buttonModifier = Modifier
//        .fillMaxWidth()
//        .padding(start = 12.dp, end = 12.dp)
//    ElevatedButton(
//        modifier = buttonModifier,
//        onClick = onMessageSent,
//    ) {
//        Text(text = "Test Messenger")
//    }
//    ElevatedButton(
//        modifier = buttonModifier,
//        onClick = onMessageSent,
//    ) {
//        Text(text = "Test AIDL")
//    }
//    onSwitchMode(IpcMode.AIDL)
}


@Composable
fun ConversationMessages(
    me: String,
    messages: List<ChatMessage>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = true,
            state = scrollState,
            contentPadding = WindowInsets.statusBars.add(WindowInsets(top = 90.dp))
                .asPaddingValues(),
        ) {
            items(messages) {
                Message(me, it, {})
            }
        }
    }
}

@Composable
private fun Message(
    me: String,
    chatMessage: ChatMessage,
    onAvatarClick: (Long) -> Unit
) {
    val isUserMe = me == chatMessage.author
    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
        if (isUserMe) {
            AuthorAndMessage(
                modifier = Modifier
                    .padding(start = 58.dp)
                    .weight(1f),
                msg = chatMessage,
                isUserMe = true,
                authorClicked = {

                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Avatar(modifier = Modifier.size(42.dp), chatMessage, true, onAvatarClick)
        } else {
            Avatar(modifier = Modifier.size(42.dp), chatMessage, false, onAvatarClick)
            Spacer(modifier = Modifier.width(16.dp))
            AuthorAndMessage(
                modifier = Modifier
                    .padding(end = 58.dp)
                    .weight(1f),
                msg = chatMessage,
                isUserMe = false,
                authorClicked = {

                }
            )
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
fun AuthorAndMessage(
    msg: ChatMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.paddingFrom(LastBaseline, after = 8.dp)
        )
        ChatItemBubble(msg, isUserMe, authorClicked = authorClicked)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val ChatBubbleShapeWithMe = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)


@Composable
fun ChatItemBubble(
    message: ChatMessage,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit // 处理 @
) {
    val backgroundBubbleColor: Color
    val shape: Shape
    if (isUserMe) {
        backgroundBubbleColor = MaterialTheme.colorScheme.primary
        shape = ChatBubbleShapeWithMe
    } else {
        backgroundBubbleColor = MaterialTheme.colorScheme.surfaceVariant
        shape = ChatBubbleShape
    }

    val modifier = Modifier.padding(16.dp)

    Surface(
        modifier = Modifier,
        color = backgroundBubbleColor,
        shape = shape
    ) {
        when (message) {
            is ChatMessage.Text -> {
                Text(
                    modifier = modifier,
                    text = "${message.author}: ${message.content}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
                )
            }

            is ChatMessage.Image -> {
                ImageItem(
                    modifier = modifier,
                    url = message.localPath ?: message.imageUrl,
                )
            }

            is ChatMessage.File -> {
                FileItem(
                    modifier = modifier,
                    url = message.localPath ?: message.url,
                    mimeType = message.mimeType,
                )
            }

        }
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