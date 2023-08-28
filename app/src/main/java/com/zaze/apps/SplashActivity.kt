package com.zaze.apps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zaze.core.designsystem.util.SplashHelper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SplashHelper.init(this)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}