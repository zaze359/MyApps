package com.zaze.apps.feature.message.data

/**
 * Description :
 * @author : zaze
 * @version : 2023-11-16 19:37
 */
data class ChatRoom(
    // 房间ID
    val id: String,
    // 房间名
    val name: String,
    // 成员列表
    val members: List<String>
)