package com.zaze.apps.widgets.dialog

import android.content.Context
import androidx.annotation.StyleRes
import android.view.Gravity
import android.view.View
import com.zaze.apps.R

/**
 * Description :
 * @author : ZAZE
 * @version : 2019-03-06 - 14:35
 */
class DialogProvider private constructor() {

    class Builder {
        @StyleRes
        internal var theme = R.style.TransparentDialog
        internal var cancelable = false
        internal var applicationOverlay = false
        internal var message: CharSequence? = null
        internal var messageGravity: Int = Gravity.CENTER
        internal var positive: String? = null
        internal var positiveListener: ((v: View) -> Unit)? = null
        internal var negative: String? = null
        internal var negativeListener: ((v: View) -> Unit)? = null
        internal var title: String? = null
        internal var tag: String? = null

        fun theme(@StyleRes theme: Int): Builder {
            this.theme = theme
            return this
        }

        fun cancelable(boolean: Boolean = true): Builder {
            this.cancelable = boolean
            return this
        }

        fun applicationOverlay(boolean: Boolean = true): Builder {
            this.applicationOverlay = boolean
            return this
        }

        fun message(message: CharSequence?, gravity: Int = Gravity.CENTER): Builder {
            this.message = message
            this.messageGravity = gravity
            return this
        }

        fun positive(
            positive: String = "确定",
            positiveListener: ((v: View) -> Unit)? = null
        ): Builder {
            this.positive = positive
            this.positiveListener = positiveListener
            return this
        }

        fun negative(
            negative: String = "取消",
            negativeListener: ((v: View) -> Unit)? = null
        ): Builder {
            this.negative = negative
            this.negativeListener = negativeListener
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun tag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun build(): CustomDialogFragment {
            return CustomDialogFragment().init(this)
        }

        fun buildCustomDialog(context: Context): CustomDialog {
            return CustomDialog(context, this)
        }
    }
}