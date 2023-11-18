package com.zaze.apps.appwidgets

import android.app.Activity
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.zaze.apps.appwidgets.compat.AppWidgetManagerCompat
import com.zaze.apps.core.base.BaseApplication

class WidgetHostViewLoader(
    private val appWidgetHost: LauncherAppWidgetHost,
    private val pInfo: PendingAddWidgetInfo
) {

    companion object {
        private const val TAG = "WidgetHostViewLoader"
        private const val LOGD = true
        const val REQUEST_CREATE_APPWIDGET = 5
    }

    private val appWidgetManager by lazy {
        AppWidgetManagerCompat.getInstance(
            BaseApplication.getInstance()
        )
    }
    private var mWidgetLoadingId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    fun startBindFlow(launcher: ActivityResultLauncher<Intent>) {
        appWidgetHost.startBindFlow(launcher, mWidgetLoadingId, pInfo.info)
    }

    fun bindWidget(): Boolean {
        if (mWidgetLoadingId <= AppWidgetManager.INVALID_APPWIDGET_ID) {
            mWidgetLoadingId = appWidgetHost.allocateAppWidgetId()
        }
        if (LOGD) {
            Log.d(TAG, "Binding widget, id: $mWidgetLoadingId");
        }
//        val option = appWidgetManager.getAppWidgetOptions(mWidgetLoadingId)
        val option = getDefaultOptionsForWidget(BaseApplication.getInstance(), pInfo)
        return appWidgetManager.bindAppWidgetIdIfAllowed(mWidgetLoadingId, pInfo.info, option)
    }

    fun inflaterWidget(): AppWidgetHostView? {
        return appWidgetHost.createView(BaseApplication.getInstance(), mWidgetLoadingId, pInfo.info)
            .apply {
                mWidgetLoadingId = AppWidgetManager.INVALID_APPWIDGET_ID
            }
    }

    fun needsConfigure(): Boolean {
        return pInfo.needsConfigure()
    }

    fun widgetItem(pm: PackageManager): WidgetItem {
        return WidgetItem(pInfo.info, pm)
    }

    fun startConfigActivity(activity: Activity, requestCode: Int): Boolean {
        try {
            appWidgetHost.startAppWidgetConfigureActivityForResult(
                activity,
                mWidgetLoadingId,
                0,
                requestCode,
                null
            )
            return true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return false
    }

    fun getDefaultOptionsForWidget(context: Context, info: PendingAddWidgetInfo): Bundle {
        val rect = Rect()
//        AppWidgetResizeFrame.getWidgetSizeRanges(context, info.spanX, info.spanY, rect)
        AppWidgetResizeFrame.getWidgetSizeRanges(
            context,
            3,
            3,
            rect
        )

        // We want to account for the extra amount of padding that we are adding to the widget
        // to ensure that it gets the full amount of space that it has requested.
        val padding =
            AppWidgetHostView.getDefaultPaddingForWidget(context, info.componentName, null)
        val density = context.resources.displayMetrics.density
        val xPaddingDips = ((padding.left + padding.right) / density).toInt()
        val yPaddingDips = ((padding.top + padding.bottom) / density).toInt()
        val options = Bundle()
        options.putInt(
            AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
            rect.left - xPaddingDips
        )
        options.putInt(
            AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
            rect.top - yPaddingDips
        )
        options.putInt(
            AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
            rect.right - xPaddingDips
        )
        options.putInt(
            AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
            rect.bottom - yPaddingDips
        )
        return options
    }

}