package com.zaze.apps.core.router

import androidx.fragment.app.Fragment

/**
 * Description : message 对外暴露的接口
 * message模块中 通过 hilt 定义注入
 * @author : zaze
 * @version : 2023-11-15 21:01
 */
interface MessageService {

    /**
     * 返回消息页
     */
    fun getMessageFragment(): Fragment

}