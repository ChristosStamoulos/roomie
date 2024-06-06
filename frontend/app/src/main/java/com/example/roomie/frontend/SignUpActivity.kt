package com.example.roomie.frontend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.roomie.R
import com.example.roomie.backend.domain.Chunk
import com.example.roomie.backend.utils.Pair
import java.util.ArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var logInButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        nameEditText = findViewById(R.id.name)
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        phoneEditText = findViewById(R.id.phone)
        confirmPasswordEditText = findViewById(R.id.conf_password)
        signUpButton = findViewById(R.id.signUp)
        logInButton = findViewById(R.id.logIn)


        signUpButton.setOnClickListener {
            signUp()
        }

        logInButton.setOnClickListener {
            logIn()
        }
    }

    private fun signUp() {
        val name = nameEditText.text.toString()
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // Validate the form
        if (validateForm(username, email, password, confirmPassword, phone)) {
            var  userData: ArrayList<Pair<String, String>> = ArrayList()

            userData.add(Pair("name", name))
            userData.add(Pair("userName", username))
            userData.add(Pair("email", email))
            userData.add(Pair("password", password))
            userData.add(Pair("phone", phone))


            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {

                val chunk = Chunk("-1", 12, userData)

                val backendCommunicator = BackendCommunicator()
                backendCommunicator.sendMasterInfo(chunk)
                val answer = backendCommunicator.sendClientInfo()
                val userId = Integer.parseInt(answer.userID)
                Log.d("Sign up", userId.toString())


                withContext(Dispatchers.Main) {
                    if (userId == -1) {
                        Toast.makeText(this@SignUpActivity, "User already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@SignUpActivity, "Sign-Up Successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@SignUpActivity, HomeScreenActivity::class.java)
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                                this@SignUpActivity,
                                R.anim.expand_trans,  // Animation for the entering activity
                                R.anim.slide_out      // Animation for the exiting activity
                        )
                        intent.putExtra("userId", userId)
                        startActivity(intent, options.toBundle())
                    }
                }
            }
        }
    }

    private fun logIn(){
        val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
                this@SignUpActivity,
                R.anim.expand_trans,  // Animation for the entering activity
                R.anim.slide_out      // Animation for the exiting activity
        )
        startActivity(intent, options.toBundle())
    }

    private fun validateForm(username: String, email: String, password: String, confirmPassword: String, phone: String): Boolean {
        if (TextUtils.isEmpty(username)) {
            usernameEditText.error = "Username is required"
            return false
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Email is required"
            return false
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            return false
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.error = "Confirm password is required"
            return false
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.error = "Phone is required"
            return false
        }

        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters long"
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return false
        }

        if (phone.length != 10) {
            phoneEditText.error = "Invalid type of phone number"
            return false
        }

        return true
    }
}