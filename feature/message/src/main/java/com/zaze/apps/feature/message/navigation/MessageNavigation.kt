package com.zaze.apps.feature.message.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.zaze.apps.feature.message.MessageRoute
import com.zaze.apps.feature.message.chat.ChatRoute
import com.zaze.apps.feature.message.data.ChatRoom
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import java.net.URLDecoder
import java.net.URLEncoder

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

const val messageRoute = "/feature/message/main"

//const val LINKED_CHAT_ROOM_ID = "linkedChatRoomId"

internal const val roomIdArg = "roomIdArg"

internal class RoomArgs(val roomId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                URLDecoder.decode(
                    checkNotNull(savedStateHandle[roomIdArg]),
                    URL_CHARACTER_ENCODING
                )
            )
}

const val chatRoomRoute = "/feature/message/chat"


fun NavController.navigateToMessage(navOptions: NavOptions? = null) {
    this.navigate(route = messageRoute, navOptions = navOptions)
}

fun NavController.navigateToChatRoom(room: ChatRoom, navOptions: NavOptions? = null) {
    this.navigate(
        route = "$chatRoomRoute/{${URLEncoder.encode(room.id, URL_CHARACTER_ENCODING)}}",
        navOptions = navOptions
    )
}

fun NavGraphBuilder.messageScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    composable(messageRoute) {
        MessageRoute(
            onBackPress = {
                navController.popBackStack()
            },
            joinRoom = {
                ZLog.i(ZTag.TAG, "navigateToChatRoom: $it")
                navController.navigateToChatRoom(it)
            },
            snackbarHostState = snackbarHostState,
        )
    }
    composable(
        route = "$chatRoomRoute/{$roomIdArg}",
        arguments = listOf(navArgument(roomIdArg) {
            type = NavType.StringType
        })
    ) {
        val roomId = it.arguments?.getString(roomIdArg) ?: "public"
        ChatRoute(
            onBackPress = {
                navController.popBackStack()
            },
            snackbarHostState = snackbarHostState,
        )
    }
}