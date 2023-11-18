package com.zaze.apps.feature.message.di

import androidx.fragment.app.Fragment
import com.zaze.apps.core.router.MessageService
import com.zaze.apps.feature.message.MessageFragment
import javax.inject.Inject

/**
 * Description :
 * @author : zaze
 * @version : 2023-11-15 22:01
 */
class MessageServiceImpl @Inject constructor() : MessageService {
    override fun getMessageFragment(): Fragment {
        return MessageFragment()
    }
}