package com.zaze.apps.feature.settings

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

/**
 * 基础 设置模板
 */
abstract class AbsSettingFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 去除分割线
        setDivider(ColorDrawable(Color.TRANSPARENT))
        dealListView()
        initPreferences()
    }

    private fun dealListView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            listView.overScrollMode = View.OVER_SCROLL_NEVER
        }
//        listView.updatePadding()
    }

    abstract fun initPreferences()

    fun setSummary(preference: Preference?) {
        preference?.let {
            updateSummary(
                it, PreferenceManager
                    .getDefaultSharedPreferences(it.context)
                    .getString(it.key, "")
            )
        }
    }

    fun updateSummary(preference: Preference, value: Any?) {
        val valueStr = value.toString()
        when (preference) {
            is ListPreference -> {
                val index = preference.findIndexOfValue(valueStr)
                preference.setSummary(if (index >= 0) preference.entries[index] else null)
            }

            else -> {
                preference.summary = valueStr
            }
        }
    }

}