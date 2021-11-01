package com.zaze.apps.widgets.dialog

import android.app.Dialog
import android.os.Build
import android.view.*
import com.zaze.apps.databinding.CustomDialogLayoutBinding
import com.zaze.apps.utils.ScreenUtils
import com.zaze.utils.DisplayUtil
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : ZAZE
 * @version : 2020-04-21 - 13:24
 */
class CustomDialogHolder {
    lateinit var binding: CustomDialogLayoutBinding

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = CustomDialogLayoutBinding.inflate(inflater, container, false)
    }

    fun onViewCreated(dialog: Dialog?, builder: DialogProvider.Builder) {
        builder.message?.let {
            binding.dialogMessageTv.gravity = builder.messageGravity
            binding.dialogMessageTv.text = it
        }
        builder.title?.let {
            binding.dialogTitleTv.visibility = View.VISIBLE
            binding.dialogTitleTv.text = it
        } ?: let {
            binding.dialogTitleTv.visibility = View.GONE
        }
        builder.negative?.let {
            binding.dialogCancelBtn.visibility = View.VISIBLE
            binding.dialogCancelBtn.text = it
        } ?: let {
            binding.dialogCancelBtn.visibility = View.GONE
        }
        builder.positive?.let {
            binding.dialogSureBtn.visibility = View.VISIBLE
            binding.dialogSureBtn.text = it
        } ?: let {
            binding.dialogSureBtn.visibility = View.GONE
        }

        binding.dialogSureBtn.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog.dismiss()
            }
            builder.positiveListener?.invoke(binding.dialogSureBtn)
        }
        binding.dialogCancelBtn.setOnClickListener {
            if (dialog?.isShowing == true) {
                dialog.dismiss()
            }
            builder.negativeListener?.invoke(binding.dialogCancelBtn)
        }
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