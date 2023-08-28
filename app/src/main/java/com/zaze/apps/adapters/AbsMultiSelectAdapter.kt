package com.zaze.apps.adapters

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.base.adapter.BaseRecyclerAdapter

abstract class AbsMultiSelectAdapter<V : Any, H : RecyclerView.ViewHolder> :
    BaseRecyclerAdapter<V, H>, ActionMode.Callback {
    private var menuRes: Int

    constructor(diffCallback: DiffUtil.ItemCallback<V>?, @MenuRes menuRes: Int) : super(
        diffCallback
    ) {
        this.menuRes = menuRes
    }

    constructor(config: AsyncDifferConfig<V>, @MenuRes menuRes: Int) : super(config) {
        this.menuRes = menuRes
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater = mode?.menuInflater
        inflater?.inflate(menuRes, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
    }
}