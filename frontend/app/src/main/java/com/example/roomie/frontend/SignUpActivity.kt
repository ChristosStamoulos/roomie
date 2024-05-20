package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.roomie.R

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
    }

    fun getUserName(): String {
        return (findViewById<EditText>(R.id.SignUpUsername)).getText().toString().trim()
    }

    fun getPassword(): String {
        return (findViewById<EditText>(R.id.edtSignUpPassword)).getText().toString().trim()
    }

    fun getEmail(): String {
        return (findViewById<EditText>(R.id.SignUpEmail)).getText().toString().trim()
    }

    fun getPasswordConfirmation(): String {
        return (findViewById<EditText>(R.id.edtSignUpConfirmPassword)).getText().toString().trim()
    }

    fun getTelephone(): String {
        return (findViewById<EditText>(R.id.edtSignUpMobile)).getText().toString().trim()
    }

    fun showMessage(title: String, msg: String){
         AlertDialog.Builder(this)
             .setCancelable(true)
             .setTitle(title)
             .setMessage(msg)
             .setPositiveButton(R.string.ok, null)
             .create()
             .show()
    }

    fun completeSignup(){
        val intent = Intent(this, HomeScreenActivity::class.java)
    }



}