package com.zaze.apps.navigation

import com.zaze.apps.R
import com.zaze.core.designsystem.compose.icon.Icon

/**
 * Description :
 * @author : zaze
 * @version : 2023-01-18 01:27
 */

enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int
) {
    /**
     * 概览
     */
    OVERVIEW(
        selectedIcon = Icon.DrawableResourceIcon(R.drawable.ic_assessment),
        unselectedIcon = Icon.DrawableResourceIcon(R.drawable.ic_assessment),
        iconTextId = R.string.overview,
        titleTextId = R.string.overview
    ),

    /**
     * 应用列表
     */
    APPS(
        selectedIcon = Icon.DrawableResourceIcon(R.drawable.ic_apps),
        unselectedIcon = Icon.DrawableResourceIcon(R.drawable.ic_apps),
        iconTextId = R.string.apps,
        titleTextId = R.string.apps
    ),

    /**
     * 消息
     */
    MESSAGE(
        selectedIcon = Icon.DrawableResourceIcon(R.drawable.ic_message),
        unselectedIcon = Icon.DrawableResourceIcon(R.drawable.ic_message),
        iconTextId = R.string.message,
        titleTextId = R.string.message
    )
}