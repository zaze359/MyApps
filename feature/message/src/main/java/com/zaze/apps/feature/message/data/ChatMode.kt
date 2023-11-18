package com.zaze.apps.feature.message.data

enum class ChatMode {
    /**
     * 使用 AIDL 方式通讯
     */
    AIDL,

    /**
     * 使用 Messenger 方式通讯
     */
    MESSENGER,

    /**
     * 使用 Broadcast 方式通讯
     */
    BROADCAST,

    /**
     * 和 服务端 通讯
     */
    SERVER,
}
