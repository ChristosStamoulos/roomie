package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomie.R
import com.example.roomie.backend.domain.Chunk
import com.example.roomie.backend.domain.Room
import com.example.roomie.backend.utils.Pair
import com.example.roomie.backend.utils.SimpleCalendar
import com.example.roomie.frontend.Adapters.ReservationsAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Reservation Activity class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented for the displayment of the user's reservations.
 */
class ReservationsActivity : AppCompatActivity(), ReservationsAdapter.onReservationClickListener {

    var bottomNavigationView: BottomNavigationView? = null
    var recyclerView: RecyclerView? = null
    var userId: Int = 0

    /**
     * Initializes the class's attributes
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        userId = intent.getIntExtra("userId", -1)

        bottomNavigationView = findViewById<View>(R.id.bottomNav) as BottomNavigationView?

        bottomNavigationView?.setSelectedItemId(R.id.reservbtn)
        bottomNavigationView!!.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            if (item.itemId == R.id.accountbtn) {
                val intent: Intent = Intent(this, MyAccountActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_in,  // Animation for the entering activity
                        R.anim.slide_out  // Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            } else if (itemId == R.id.homeBtn) {
                val intent: Intent = Intent(this, HomeScreenActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_out,  // Animation for the entering activity
                        R.anim.slide_in// Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            } else if (itemId == R.id.searchbtn) {
                val intent: Intent = Intent(this, SearchActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_out,  // Animation for the entering activity
                        R.anim.slide_in// Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            }
            item.setChecked(true)
            true
        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)!!
        val grid = GridLayoutManager(this, 1)
        recyclerView!!.layoutManager = grid

        //asynchronous routine
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {

            var res = ArrayList<Pair<Pair<Room, ArrayList<ByteArray>>,ArrayList<SimpleCalendar>>>()
            val chunk = Chunk("1", 10, userId)

            val backendCommunicator = BackendCommunicator()
            backendCommunicator.sendMasterInfo(chunk)
            val answer = backendCommunicator.sendClientInfo()
            val reservations = answer.data as ArrayList<Pair<Integer, ArrayList<SimpleCalendar>>>

            for(pair in reservations){
                val roomId = pair.key
                val dates = pair.value
                val chunk1 = Chunk("", 9, roomId)

                val bc = BackendCommunicator()
                bc.sendMasterInfo(chunk1)
                val answer1 = bc.sendClientInfo()
                val room_details = answer1.data as Pair<Room, ArrayList<ByteArray>>

                var room = room_details.key
                var imgs = room_details.value

                var p = Pair(Pair(room, imgs), dates)
                res.add(p)
            }


            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {
                val reservationsAdapter = ReservationsAdapter(res, this@ReservationsActivity)
                recyclerView!!.adapter = reservationsAdapter
            }

        }
    }

    /**
     * Starts Room Details activity and passes the corresponding room id.
     * @param room
     * @param view
     */
    override fun onReservationClick(room: Room, view: View) {
        var intent = Intent(this, RoomDetailsActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.expand_trans,  // Animation for the entering activity
                R.anim.slide_out  // Animation for the exiting activity
        )
        intent.putExtra("roomId", room.id)
        startActivity(intent, options.toBundle())
    }
}