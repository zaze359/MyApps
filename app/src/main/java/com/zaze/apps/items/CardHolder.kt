package com.zaze.apps.items

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:33
 */
abstract class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun setup(value: CardItem)
}