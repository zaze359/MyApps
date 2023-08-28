package com.zaze.apps.navigation

import android.graphics.drawable.Icon
import com.zaze.apps.R

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
//    ONE(
//        selectedIcon = Icon.ImageVectorIcon(MyIcons.ViewDay),
//        unselectedIcon = Icon.ImageVectorIcon(MyIcons.ViewDay),
//        iconTextId = R.string.app_name,
//        titleTextId = R.string.app_name
//    ),
//    TWO(
//        selectedIcon = Icon.ImageVectorIcon(MyIcons.Person),
//        unselectedIcon = Icon.ImageVectorIcon(MyIcons.Person),
//        iconTextId = R.string.app_name,
//        titleTextId = R.string.app_name
//    ),
//    THREE(
//        selectedIcon = Icon.ImageVectorIcon(MyIcons.Grid3x3),
//        unselectedIcon = Icon.ImageVectorIcon(MyIcons.Grid3x3),
//        iconTextId = R.string.app_name,
//        titleTextId = R.string.app_name
//    )
}