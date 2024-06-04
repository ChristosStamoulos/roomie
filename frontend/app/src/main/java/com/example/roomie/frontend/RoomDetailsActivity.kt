package com.example.roomie.frontend

import com.example.roomie.frontend.utils.AvailabilityDecorator
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.roomie.R
import com.example.roomie.backend.domain.Chunk
import com.example.roomie.backend.domain.Room
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.HashSet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class RoomDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    var backendCommunicator: BackendCommunicator? = null
    var room: Room? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)

        val roomId = intent.getIntExtra("roomId", -1)

        val chunk = Chunk("", 9, roomId)
        val backendCommunicator = BackendCommunicator()
        backendCommunicator.attemptConnection()
        backendCommunicator.sendMasterInfo(chunk)

        val room_details = backendCommunicator.sendClientInfo()
        room = room_details.data as Room

        setDetails()

        val calendarView = findViewById<MaterialCalendarView>(R.id.calendar_view)
        val availableDates = getAvailableDates(room!!)
        calendarView.addDecorators(AvailabilityDecorator(availableDates))

        calendarView.setOnDateChangedListener { widget, date, selected ->
            // Handle date selection
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(38.027485, 23.746913) // Replace with the actual location
        googleMap.addMarker(MarkerOptions().position(location).title("Room Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun getAvailableDates(room: Room): HashSet<CalendarDay> {
        // Example of available dates
        val dates = HashSet<CalendarDay>()
        val d = room.availableDates
        for (dat in d){
            dates.add(CalendarDay.from(dat.year,dat.month,dat.dayOfMonth))
        }
        return dates
    }

    private fun setDetails(){
        val price = findViewById<TextView>(R.id.price_value)
        val name = findViewById<TextView>(R.id.room_name)
        val location = findViewById<TextView>(R.id.location)

        price.text = room!!.price.toString()
        name.text = room!!.name
        location.text = room!!.area
    }
}