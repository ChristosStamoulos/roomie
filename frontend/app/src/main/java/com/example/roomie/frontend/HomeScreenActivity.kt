package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomie.R
import com.example.roomie.backend.domain.Room
import com.example.roomie.frontend.Adapters.RoomsAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class HomeScreenActivity : AppCompatActivity(), RoomsAdapter.onRoomClickListener {

    var bottomNavigationView: BottomNavigationView? = null
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        bottomNavigationView = findViewById<View>(R.id.bottomNav) as BottomNavigationView?

        bottomNavigationView?.setSelectedItemId(R.id.homeBtn)
        bottomNavigationView!!.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            if (item.itemId == R.id.accountbtn) {
                val switchToStatisticsActivity: Intent = Intent(this, HomeScreenActivity::class.java)//StatisticsActivity::class.java)
                startActivity(switchToStatisticsActivity)
            } else if (itemId == R.id.reservbtn) {
                val switchToLoginActivity: Intent = Intent(this, HomeScreenActivity::class.java)//LoginActivity::class.java)
                startActivity(switchToLoginActivity)
            }
            item.setChecked(true)
            true
        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)!!
        val grid = GridLayoutManager(this,2)
        recyclerView!!.layoutManager = grid

        val rt = RoomTester()
        val rooms = rt.getRooms()
        Log.d("HomeScreen", rooms.toString())
        val roomsAdapter = RoomsAdapter(rooms, this)
        recyclerView!!.adapter = roomsAdapter

    }

    override fun onRoomClick(room: Room, view: View) {
        var intent = Intent(this, RoomDetailsActivity::class.java)
        startActivity(intent)
    }
}