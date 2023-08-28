package com.zaze.apps.widget.dialog

import android.app.Dialog
import android.os.Build
import android.view.*
import com.zaze.apps.utils.ScreenUtils
import com.zaze.utils.DisplayUtil
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : zaze
 * @version : 2021-11-30 - 10:45
 */
abstract class DialogViewHolder {
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?) {
    }

    fun onViewCreated(dialog: Dialog?, builder: DialogProvider.Builder) {
    }

    fun onCreateDialog(dialog: Dialog, builder: DialogProvider.Builder) {
        dialog.setCancelable(builder.cancelable)
        dialog.window?.let { window ->
            val systemUiVisibility = ScreenUtils.addLayoutFullScreen(window, true)
            window.decorView.setOnApplyWindowInsetsListener { v, insets ->
                ZLog.i(ZTag.TAG, "dialog setOnApplyWindowInsetsListener insets: $insets")
                insets
            }
            // --------------------------------------------------
            window.decorView.setOnSystemUiVisibilityChangeListener {
                ZLog.i(ZTag.TAG, "dialog visibility: ${it == View.SYSTEM_UI_FLAG_VISIBLE}")
                window.decorView.systemUiVisibility = systemUiVisibility
            }
            if (builder.applicationOverlay) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                } else {
                    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                }
                window.setGravity(Gravity.CENTER)
            }
        }
    }

    fun measure(dialog: Dialog?) {
        dialog?.window?.setLayout(
            DisplayUtil.pxFromDp(480F).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}