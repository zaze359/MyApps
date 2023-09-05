package com.zaze.apps.data

sealed interface AppSort {
    object Name: AppSort
    object Size: AppSort
    object InstallTime: AppSort
    object UpdateTime: AppSort
//    object UsedTime: AppSort
}