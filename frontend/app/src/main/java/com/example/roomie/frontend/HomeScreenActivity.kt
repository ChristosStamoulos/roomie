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
import com.example.roomie.frontend.Adapters.RoomsAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/** Home Screen Activity class
 *
 * @author Maria Schoinaki, Eleni Kechrioti, Christos Stamoulos
 * @Details This project is being carried out in the course Distributed Systems @ Spring AUEB 2024.
 *
 * This class is implemented to visualize an implemented home screen.
 */
class HomeScreenActivity : AppCompatActivity(), RoomsAdapter.onRoomClickListener {

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

        bottomNavigationView?.setSelectedItemId(R.id.homeBtn)
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
            } else if (itemId == R.id.reservbtn) {
                val intent: Intent = Intent(this, ReservationsActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_in,  // Animation for the entering activity
                        R.anim.slide_out  // Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            } else if (itemId == R.id.searchbtn) {
                val intent: Intent = Intent(this, SearchActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_in,  // Animation for the entering activity
                        R.anim.slide_out  // Animation for the exiting activity
                )
                intent.putExtra("userId", userId)
                startActivity(intent, options.toBundle())
            }
            item.setChecked(true)
            true
        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)!!
        val grid = GridLayoutManager(this, 2)
        recyclerView!!.layoutManager = grid
        var rooms: ArrayList<Pair<Room, ArrayList<ByteArray>>>

        //asynchronous routine
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {

            val chunk = Chunk(userId.toString(), 1, generateFilterAll().toString())

            val backendCommunicator = BackendCommunicator()
            backendCommunicator.sendMasterInfo(chunk)
            val answer = backendCommunicator.sendClientInfo()
            rooms = answer.data as ArrayList<Pair<Room, ArrayList<ByteArray>>>


            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {
                val roomsAdapter = RoomsAdapter(rooms, this@HomeScreenActivity)
                recyclerView!!.adapter = roomsAdapter
            }

        }
    }

    /**
     * Starts Room Details activity and passes the corresponding room id.
     * @param room
     * @param view
     */
    override fun onRoomClick(room: Room, view: View) {
        var intent = Intent(this, RoomDetailsActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.expand_trans,  // Animation for the entering activity
                R.anim.slide_out  // Animation for the exiting activity
        )
        intent.putExtra("roomId", room.id)
        intent.putExtra("userId", userId)
        startActivity(intent, options.toBundle())
        //startActivity(intent)
    }

    /**
     * Generates the default values of a room,
     * so all the rooms can be displayed in Home Screen.
     */
    fun generateFilterAll(): JSONObject{
        var filter = JSONObject()
        filter.put("area", "default")
        filter.put("startDate", "01/01/0001")
        filter.put("finishDate", "01/01/0001")
        filter.put("noOfPeople", 0)
        filter.put("lowPrice", 0)
        filter.put("highPrice", 0)
        filter.put("stars", 0.0)

        var f = JSONObject()
        f.put("filters", filter)
        return f
    }
}