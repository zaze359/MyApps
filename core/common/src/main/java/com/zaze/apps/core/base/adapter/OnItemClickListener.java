package com.zaze.apps.core.base.adapter;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Description :
 *
 * @author : ZAZE
 * @version : 2016-12-12 - 00:46
 */
public interface OnItemClickListener<V> {
    /**
     * 点击item回调
     *
     * @param view     view
     * @param value    value
     */
    void onItemClick(@NonNull View view, V value);
}
