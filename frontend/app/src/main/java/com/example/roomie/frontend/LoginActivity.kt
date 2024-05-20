package com.example.roomie.frontend

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.roomie.R

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
    }



    fun getUserName(): String {
        return (findViewById<EditText>(R.id.SignUpUsername)).getText().toString().trim()
    }

    fun getPassword(): String {
        return (findViewById<EditText>(R.id.edtSignUpPassword)).getText().toString().trim()
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
}