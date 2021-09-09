package com.zaze.apps.items

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.data.Card
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:33
 */
abstract class CardHolder<out V : Card>(itemView:View) : RecyclerView.ViewHolder(itemView) {

    fun setup(value: Card) {
        try {
            doSetup(value as @UnsafeVariance V)
        } catch (e: Exception) {
            ZLog.w(ZTag.TAG, "内容不匹配")
        }
    }

    abstract fun doSetup(value: @UnsafeVariance V)

}