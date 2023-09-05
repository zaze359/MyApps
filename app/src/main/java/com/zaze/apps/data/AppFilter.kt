package com.zaze.apps.data

sealed class AppFilter(val name: String) {
    /**
     * 全部
     */
    object ALL : AppFilter("全部应用")

    /** 用户 */
    object USER : AppFilter("用户应用")

    /** 系统 */
    object SYSTEM : AppFilter("系统应用")

    /** 冻结 */
    object FROZEN : AppFilter("已禁用")

    /** apk */
    object APK : AppFilter("安装包")
}