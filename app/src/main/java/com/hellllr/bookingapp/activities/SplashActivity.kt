package com.hellllr.bookingapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.hellllr.bookingapp.MainActivity
import com.hellllr.bookingapp.R

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashScreenDuration = 1000L

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            if (auth.currentUser != null) {
                // User is signed in, navigate to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not signed in, navigate to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, splashScreenDuration)
    }
}
