package com.codermonkeys.trelloclone.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codermonkeys.trelloclone.R

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TRANSITION_TIME = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }, TRANSITION_TIME)
    }

    override fun onResume() {
        super.onResume()
        //Hide the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}