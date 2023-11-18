package com.zaze.apps

import androidx.fragment.app.Fragment
import com.zaze.apps.core.router.MessageService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Description : 占位用的Fragment
 * @author : zaze
 * @version : 2023-11-13 23:37
 */
@AndroidEntryPoint
class MessageFragment : RouteFragment() {

    @Inject
    lateinit var messageService: MessageService
    override fun getFragment(): Fragment {
        return messageService.getMessageFragment()
    }
}