package com.zaze.apps.feature.settings

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.zaze.apps.core.base.AbsActivity
import com.zaze.apps.core.ext.findFragment
import com.zaze.apps.core.ext.replaceFragment
import com.zaze.apps.core.ext.findNavHostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AbsActivity() {

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 处理内部导航逻辑
            if (findFragment(R.id.container)?.findNavHostFragment(R.id.fragment_container)?.navController?.navigateUp() != true) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity_settings)
        val fragment = findFragment(R.id.container) ?: SettingsNavFragment()
        replaceFragment(R.id.container, fragment)
        this.onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }
}