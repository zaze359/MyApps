package com.zaze.apps

import com.zaze.apps.utils.AppShortcut

interface AppOperator {
    fun getApk(appShortcut: AppShortcut)
}