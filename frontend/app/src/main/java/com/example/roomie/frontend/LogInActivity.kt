package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.roomie.R
import com.example.roomie.backend.domain.Chunk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/** Log In Activity class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented for the log in page of the app.
 */
class LogInActivity : AppCompatActivity() {

    /**
     * Initializes the class's attributes
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        signUpButton()
        logInButton()
    }


    /**
     * Sends to Master the credentials and checks
     * if the user exists or not
     */
    fun logI(){
        var credentials = ArrayList<String>()
        credentials.add(getUserName())
        credentials.add(getPassword())
        val chunk = Chunk("-1", 11, credentials)

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {

            val backendCommunicator = BackendCommunicator()
            backendCommunicator.sendMasterInfo(chunk)
            val answer = backendCommunicator.sendClientInfo()
            var userId = Integer.parseInt(answer.userID)

            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {
                if(userId == -1) {
                    showMessage("Error", "You are not registered!")
                }else{
                    logIn(userId)
                }
            }
        }
    }

    /**
     * Sets a listener for when a user clicks
     * the log in button
     */
    fun logInButton(){
        var logInbtn = findViewById<Button>(R.id.logInbtn)

        logInbtn.setOnClickListener(){
            if(validateCredentials(getUserName(), getPassword())){
                logI()
            }
        }
    }

    /**
     * Validates the user's credentials
     *
     * @param username  the user's username
     * @param password  the user's password
     */
    fun validateCredentials(username: String, password: String): Boolean{
        if(username.equals("") || username == null){
            showMessage("Error", "The username is not filled!")
        }else if(password.equals("") || password == null){
            showMessage("Error", "The password is not filled!")
        }else{
            return true
        }
        return false
    }

    /**
     * Sets a listener for when a user clicks
     * the sign up button
     */
    fun signUpButton(){
        var signUpbtn = findViewById<TextView>(R.id.signUpbtn)

        signUpbtn.setOnClickListener(){
            signUp()
        }
    }

    /**
     * Navigates the user to the HomeScreen
     *
     * @param userId    the user's id
     */
    fun logIn(userId: Int){
        var intent = Intent(this, HomeScreenActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.fade_in,  // Animation for the entering activity
                R.anim.fade_out  // Animation for the exiting activity
        )
        intent.putExtra("userId", userId)
        startActivity(intent, options.toBundle())
    }

    /**
     * Navigates the user to the Sign Up screen
     */
    fun signUp(){
        var intent = Intent(this, SignUpActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.fade_in,  // Animation for the entering activity
                R.anim.fade_out  // Animation for the exiting activity
        )
        startActivity(intent, options.toBundle())
    }

    /**
     * Gets the username the user typed
     *
     * @return  the username as String
     */
    fun getUserName(): String {
        return (findViewById<EditText>(R.id.usernamebtn)).getText().toString().trim()
    }

    /**
     * Gets the password the use typed
     *
     * @return  the password as String
     */
    fun getPassword(): String {
        return (findViewById<EditText>(R.id.passwordbtn)).getText().toString().trim()
    }

    /**
     * Displays a message to the screen
     *
     * @param title the message's title
     * @param msg   the message
     */
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