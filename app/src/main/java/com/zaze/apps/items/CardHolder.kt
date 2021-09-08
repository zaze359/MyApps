package com.zaze.apps.items

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:33
 */
abstract class CardHolder<out V : CardItem>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {


    inline fun <reified T : @UnsafeVariance V> setup(value: CardItem) {
        if (value !is T) {
            return
        }
        doSetup(value)
    }

    abstract fun doSetup(value: @UnsafeVariance V)

}