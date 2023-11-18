package com.zaze.apps

import com.zaze.core.common.utils.app.AppShortcut


interface AppOperator {
    fun getApk(appShortcut: AppShortcut)
}