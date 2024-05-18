package com.example.chambre.frontend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chambre.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.HashSet

class RoomDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)

        val calendarView = findViewById<MaterialCalendarView>(R.id.calendar_view)
        val availableDates = getAvailableDates()
        calendarView.addDecorators(AvailabilityDecorator(availableDates))

        calendarView.setOnDateChangedListener { widget, date, selected ->
            // Handle date selection
        }
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
