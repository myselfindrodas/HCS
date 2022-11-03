package com.app.hcsassist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.wemu.session.SessionManager

class Splash : AppCompatActivity() {
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sessionManager = SessionManager(this)

        val secondsDelayed = 1
        Handler().postDelayed({
            if (sessionManager!!.isLoggedIn) {
                val intent = Intent(this@Splash, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@Splash, Login::class.java)
                startActivity(intent)
                finish()
            }
        }, (secondsDelayed * 3000).toLong())
    }
}