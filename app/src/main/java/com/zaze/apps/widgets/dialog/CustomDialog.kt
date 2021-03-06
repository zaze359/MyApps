package com.zaze.apps.widgets.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : ZAZE
 * @version : 2019-03-06 - 14:35
 */
class CustomDialog internal constructor(context: Context, builder: DialogProvider.Builder) {
    private val dialog = Dialog(context, builder.theme)
    private val customDialogHolder = CustomDialogHolder()

    init {
        customDialogHolder.onCreateDialog(dialog, builder)
        customDialogHolder.onCreateView(LayoutInflater.from(context), null)
        customDialogHolder.onViewCreated(dialog, builder)
        dialog.setContentView(customDialogHolder.binding.root)
    }

    fun show() {
        val context = dialog.context
        if (context is Activity && context.isFinishing) {
            ZLog.w(ZTag.TAG_ERROR, "Activity isFinishing return it!")
            return
        }
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.show()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        customDialogHolder.measure(dialog)
    }

    fun dismiss() {
        dialog.dismiss()
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }
}