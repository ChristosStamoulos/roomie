package com.example.roomie.frontend

import com.example.roomie.frontend.utils.AvailabilityDecorator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.roomie.R
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)

        val calendarView = findViewById<MaterialCalendarView>(R.id.calendar_view)
        val availableDates = getAvailableDates()
        calendarView.addDecorators(AvailabilityDecorator(availableDates))

        calendarView.setOnDateChangedListener { widget, date, selected ->
            // Handle date selection
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(37.7749, -122.4194) // Replace with the actual location
        googleMap.addMarker(MarkerOptions().position(location).title("Room Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun getAvailableDates(): HashSet<CalendarDay> {
        // Example of available dates
        val dates = HashSet<CalendarDay>()
        dates.add(CalendarDay.from(2024, 5, 20))
        dates.add(CalendarDay.from(2024, 5, 21))
        dates.add(CalendarDay.from(2024, 5, 22))
        return dates
    }
}