package com.chekinlite.app.CurrentVersion.Activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.chekinlite.app.CurrentVersion.NavigationMenu.NavigationAcitivity
import com.chekinlite.app.R


class SplashScreenActivity : AppCompatActivity() {

    lateinit var logoChekin: ImageView
    lateinit var background: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        logoChekin = findViewById(R.id.logoChekinImage)
        background = findViewById(R.id.backgroundWhite)


        var sharedPreference = getSharedPreferences("LOGIN_CREDENTIALS", Context.MODE_PRIVATE)

        if (sharedPreference.getBoolean("Logged", false)) {
            val handle = Handler()
            handle.postDelayed({
                val intent = Intent(this@SplashScreenActivity, NavigationAcitivity::class.java)
                startActivity(intent)
                this@SplashScreenActivity.finish()
            }, 2000)

        } else {
            val handle = Handler()
            handle.postDelayed({
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                this@SplashScreenActivity.finish()
            }, 2000)
        }

    }
}
