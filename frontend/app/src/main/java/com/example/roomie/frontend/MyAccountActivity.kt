package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.roomie.R
import com.example.roomie.backend.domain.Chunk
import com.example.roomie.backend.domain.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** My Account Activity class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented for the user's account page of the app.
 */
class MyAccountActivity: AppCompatActivity() {

    var bottomNavigationView: BottomNavigationView? = null
    var userId: Int = 0

    /**
     * Initializes the class's attributes
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_account)

        userId = intent.getIntExtra("userId", -1)

        bottomNavigationView = findViewById<View>(R.id.bottomNav) as BottomNavigationView?

        bottomNavigationView?.setSelectedItemId(R.id.accountbtn)
        bottomNavigationView!!.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            if (item.itemId == R.id.homeBtn) {
                val intent: Intent = Intent(this, HomeScreenActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_out ,  // Animation for the entering activity
                        R.anim.slide_in // Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            } else if (itemId == R.id.reservbtn) {
                val intent: Intent = Intent(this, ReservationsActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_out ,  // Animation for the entering activity
                        R.anim.slide_in // Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            } else if (itemId == R.id.searchbtn) {
                val intent: Intent = Intent(this, SearchActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_out ,  // Animation for the entering activity
                        R.anim.slide_in // Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            }
            item.setChecked(true)
            true
        })
        logOut()
        getUserDetails()
    }

    /**
     * Navigates the user to the Start screen
     */
    fun logOut(){
        var logoutbtn = findViewById<Button>(R.id.logout)
        logoutbtn.setOnClickListener(){
            var intent = Intent(this, StartScreenActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Gets user's details from master
     */
    fun getUserDetails(){
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {

            val chunk = Chunk(userId.toString(), 13, userId)

            val backendCommunicator = BackendCommunicator()
            backendCommunicator.sendMasterInfo(chunk)
            val answer = backendCommunicator.sendClientInfo()
            var user = answer.data as User

            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {
                setDetails(user)
            }
        }
    }

    /**
     * Sets the user's details
     */
    fun setDetails(user: User){
        var fullname = findViewById<TextView>(R.id.fullnameacc)
        var phone = findViewById<TextView>(R.id.phonenumberacc)
        var email = findViewById<TextView>(R.id.emailacc)
        var username = findViewById<TextView>(R.id.usernameacc)

        fullname.text = user.name
        phone.text = user.phoneNumber
        email.text = user.email
        username.text = user.username
    }
}