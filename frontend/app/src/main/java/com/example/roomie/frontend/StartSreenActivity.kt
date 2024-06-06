package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.roomie.R

class StartScreenActivity : AppCompatActivity() {
    private lateinit var signUpButton: TextView
    private lateinit var logInButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_start_screen)

        signUpButton = findViewById(R.id.btnSignUp)
        logInButton = findViewById(R.id.btnLogIn)

        signUpButton.setOnClickListener {
            signUp()
        }

        logInButton.setOnClickListener {
            logIn()
        }
    }

    private fun signUp(){
        val intent = Intent(this@StartScreenActivity, SignUpActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
                this@StartScreenActivity,
                R.anim.expand_trans,  // Animation for the entering activity
                R.anim.slide_out      // Animation for the exiting activity
        )
        startActivity(intent, options.toBundle())
    }

    private fun logIn(){
        val intent = Intent(this@StartScreenActivity, LogInActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
                this@StartScreenActivity,
                R.anim.expand_trans,  // Animation for the entering activity
                R.anim.slide_out      // Animation for the exiting activity
        )
        startActivity(intent, options.toBundle())
    }
}