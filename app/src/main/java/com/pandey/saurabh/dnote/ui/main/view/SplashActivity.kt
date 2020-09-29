package com.pandey.saurabh.dnote.ui.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.pandey.saurabh.dnote.R

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long = 3000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

       // val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_animation)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity


            startActivity(Intent(this, MainActivity::class.java))

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }
    }


