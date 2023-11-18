package com.zaze.apps.viewmodels

import android.appwidget.AppWidgetHostView
import androidx.lifecycle.viewModelScope
import com.zaze.apps.appwidgets.*
import com.zaze.apps.appwidgets.compat.AppWidgetManagerCompat
import com.zaze.apps.core.base.AbsViewModel
import com.zaze.apps.core.base.BaseApplication
import com.zaze.core.common.utils.app.AppShortcut
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-21 - 15:57
 */
class AppWidgetsViewModel : AbsViewModel() {

    private val _appShortcut = MutableStateFlow<AppShortcut?>(null)
    val appShortcut: Flow<AppShortcut?> = _appShortcut

    /**
     * app widgets
     */
    private val _appWidgets = MutableStateFlow<List<AppWidgetHostView>>(emptyList())
    val appWidgets: Flow<List<AppWidgetHostView>> = _appWidgets
    private val waitingLoaders = Stack<WidgetHostViewLoader>()
    val bindWidgetAction = Channel<WidgetHostViewLoader>()
    val startWidgetConfigAction = Channel<WidgetHostViewLoader>()
    private val appWidgetHost by lazy {
        LauncherAppWidgetHost(BaseApplication.getInstance())
    }


    fun preloadAppWidgets() {
        appWidgetHost.startListening()
        viewModelScope.launch(Dispatchers.Default) {
            waitingLoaders.clear()
            // --------------------------------------------------
            val context = BaseApplication.getInstance()
            val appWidgetManager = AppWidgetManagerCompat.getInstance(context)
            appWidgetManager.getAllProviders(null)
                .forEach { pInfo ->
                    val loader = WidgetHostViewLoader(
                        appWidgetHost,
                        PendingAddWidgetInfo(
                            LauncherAppWidgetProviderInfo.fromProviderInfo(pInfo)
                        )
                    )
                    waitingLoaders.add(loader)
                }
            bind()
        }
    }

    private suspend fun requestBindWidget() {
        if (waitingLoaders.empty()) {
            return
        }
        bindWidgetAction.send(waitingLoaders.peek())
    }

    private fun inflaterWidget() {
        if (waitingLoaders.isEmpty()) {
            return
        }
        val loader = waitingLoaders.peek()
        loader.inflaterWidget()?.let {
            _appWidgets.value = _appWidgets.value.toMutableList().apply {
                this.add(it)
            }
        }
    }

//    suspend fun startWidgetConfig() {
//        if (waitingLoaders.empty()) {
//            return
//        }
//        val loader = waitingLoaders.peek()
//        if (loader.needsConfigure()) {
//            // 需要打开配置，暂不处理直接跳过
//            bindNext()
//        } else {
//            bind()
//        }
//    }

    private suspend fun bind() {
        val loader = waitingLoaders.peek()
        when {
            !loader.bindWidget() -> {// 无法绑定, 请求
                requestBindWidget()
            }
            else -> {
                addAppWidget()
            }
        }
    }

    suspend fun addAppWidget() {
        val loader = waitingLoaders.peek()
        when {
            loader.needsConfigure() -> {
                ZLog.i(
                    ZTag.TAG,
                    "addAppWidget ${loader.widgetItem(BaseApplication.getInstance().packageManager).label}: 需要打开配置，暂不处理直接跳过"
                )
                bindNext()
            }
            else -> {
                ZLog.i(
                    ZTag.TAG,
                    "addAppWidget ${loader.widgetItem(BaseApplication.getInstance().packageManager).label}"
                )
                inflaterWidget()
                bindNext()
            }
        }
    }

    suspend fun bindNext() {
        if (waitingLoaders.isEmpty()) {
            return
        }
        waitingLoaders.pop()
        if (waitingLoaders.isNotEmpty()) {
            bind()
        }
    }

    override fun onCleared() {
        super.onCleared()
        appWidgetHost.stopListening()
    }
}