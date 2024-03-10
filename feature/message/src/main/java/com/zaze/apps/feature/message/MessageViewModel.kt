package com.zaze.apps.feature.message

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import androidx.lifecycle.viewModelScope
import com.zaze.apps.core.base.AbsAndroidViewModel
import com.zaze.apps.feature.message.data.ChatMessage
import com.zaze.apps.feature.message.data.ChatMode
import com.zaze.apps.feature.message.data.ChatRoom
import com.zaze.apps.feature.message.data.getMessageContent
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import com.zaze.utils.query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(application: Application) :
    AbsAndroidViewModel(application) {

    private val viewModelState = MutableStateFlow(MessageViewModelState())
    val uiState: StateFlow<MessageUiState> =
        viewModelState.map(MessageViewModelState::toUiState).stateIn(
            viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState()
        )

    /** 发送数据 */
    var serviceMessenger: Messenger? = null

    /** 接收服务端回执 */
    private var messengerThread = HandlerThread("messenger_thread").apply { start() }
    private val clientMessenger = Messenger(object : Handler(messengerThread.looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
//            addChatMessage(
//                ChatMessage.Text(
//                    author = "messenger",
//                    content = "${msg.data.getString("replay")}",
//                )
//            )
        }
    })

//    /** aidl 接口 */
//    private var remoteService: IRemoteService? = null
//    private val remoteServiceDeathRecipient = IBinder.DeathRecipient {
//        ZLog.i("MessageViewModel", "remoteService binderDied")
//        onRemoteServiceDisconnected()
//        // 重连？
//    }

    fun onRemoteServiceConnected(service: IBinder?) {
//        remoteService = IRemoteService.Stub.asInterface(service)
//        service?.linkToDeath(remoteServiceDeathRecipient, 0)
    }

    fun onRemoteServiceDisconnected() {
        ZLog.i("MessageViewModel", "onRemoteServiceDisconnected")
//        remoteService?.asBinder()?.unlinkToDeath(remoteServiceDeathRecipient, 0)
//        remoteService = null
    }

    init {
        viewModelScope.launch {
            repeat(10) {
                addChatRoom(
                    ChatRoom(
                        id = "$it",
                        name = "room$it",
                        members = emptyList(),
                    )
                )
            }
        }
    }


    fun sendMessage(message: String) {
        val chatMessage = ChatMessage.Text(
            author = "me",
            content = message,
        )
        actualSend(chatMessage)
    }

    private fun actualSend(message: ChatMessage) {
//        addChatMessage(message)
//        val messageContent = message.getMessageContent()
//        when (viewModelState.value.messageMode) {
//            MessageMode.AIDL -> {
////                addChatMessage(
////                    ChatMessage.Text(
////                        author = "aidl",
////                        content = "${remoteService?.messageService?.message?.data}",
////                    )
////                )
//            }
//
//            MessageMode.MESSENGER -> {
//                val msg = Message.obtain()
//                msg.replyTo = clientMessenger
//                val bundle = Bundle()
//                bundle.putString("content", messageContent)
//                msg.data = bundle
//                serviceMessenger?.send(msg)
//            }
//
//            MessageMode.BROADCAST -> {
////                application.sendBroadcast(Intent(MessageReceiver.ACTION_MESSAGE).also {
////                    it.putExtra(MessageReceiver.KEY_MESSAGE, IpcMessage(data = messageContent))
////                })
//            }
//
//            MessageMode.SERVER -> {
////                application.sendBroadcast(Intent(MessageReceiver.ACTION_MESSAGE).also {
////                    it.putExtra(MessageReceiver.KEY_MESSAGE, IpcMessage(data = messageContent))
////                })
//            }
//
//            else -> {
//                addChatMessage(
//                    ChatMessage.Text(
//                        author = "error",
//                        content = "暂未实现该功能",
//                    )
//                )
//            }
//        }
    }

    fun sendPicture(filePath: String) {
        val chatMessage = ChatMessage.Image(
            author = "me",
            localPath = filePath,
        )
        actualSend(chatMessage)
    }

    fun onFileSend(files: List<String>) {
        viewModelScope.launch {
            files.forEach {
                val uri = Uri.parse(it)
                println("uri: $uri")
                if (DocumentsContract.isDocumentUri(application, uri)) {
                    // document类型，通过documentId 查询
                    val docId = DocumentsContract.getDocumentId(uri)
                }
                println("getType: ${application.contentResolver.getType(uri)}")
                uri.query(context = application) { cursor ->
                    while (cursor.moveToNext()) {
                        cursor.columnNames.forEach { name ->
                            val index = cursor.getColumnIndex(name)
                            val value: Any? = if (index >= 0) {
                                when (cursor.getType(index)) {
                                    Cursor.FIELD_TYPE_NULL -> {
                                        null
                                    }

                                    Cursor.FIELD_TYPE_INTEGER -> {
                                        cursor.getInt(index)
                                    }

                                    Cursor.FIELD_TYPE_FLOAT -> {
                                        cursor.getFloat(index)

                                    }

                                    Cursor.FIELD_TYPE_STRING -> {
                                        cursor.getString(index)
                                    }

                                    Cursor.FIELD_TYPE_BLOB -> {
                                        cursor.getBlob(index)
                                    }

                                    else -> {
                                        null
                                    }
                                }
                            } else {
                                null
                            }
                            println("columnNames: $name = $value")
                        }
                    }
                }

                val chatMessage = ChatMessage.File(
                    author = "me",
                    localPath = it,
                    mimeType = application.contentResolver.getType(uri)
                )
                actualSend(chatMessage)
            }
        }

    }

    fun onSwitchMode(messageMode: ChatMode) {
        ZLog.i(ZTag.TAG + "IpcViewModel", "onSwitchMode: $messageMode")
    }

    fun addChatRoom(room: ChatRoom) {
        ZLog.i(
            ZTag.TAG + "Message",
            "addChatRoom ${viewModelState.value.chatRooms.size}: $room"
        )
        viewModelState.update {
            val newList = it.chatRooms.toMutableList()
            newList.add(room)
            it.copy(chatRooms = newList)
        }
    }
}

private data class MessageViewModelState(
    val chatRooms: List<ChatRoom> = emptyList(),
    val me: String = "me",
) {
    fun toUiState(): MessageUiState {
        return MessageUiState(
            chatRooms = chatRooms,
            me = me
        )
    }
}

data class MessageUiState(
    val chatRooms: List<ChatRoom>,
    val me: String
)