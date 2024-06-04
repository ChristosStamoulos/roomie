package com.example.roomie.frontend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.roomie.R
import com.example.roomie.backend.domain.Chunk
import com.example.roomie.backend.domain.Room
import com.example.roomie.backend.utils.Pair
import com.example.roomie.frontend.utils.AvailabilityDecorator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class RoomDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    var backendCommunicator: BackendCommunicator? = null
    var imgs: ArrayList<ByteArray>? = null
    var room: Room? = null
    var calendarView: MaterialCalendarView? = null
    private var startDate: CalendarDay? = null
    private var finishDate: CalendarDay? = null
    var avDates: HashSet<CalendarDay>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)

        val roomId = intent.getIntExtra("roomId", -1)


        //asynchronous routine
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {

            val chunk = Chunk("", 9, roomId)

            val backendCommunicator = BackendCommunicator()
            backendCommunicator.sendMasterInfo(chunk)

            val dat = backendCommunicator.sendClientInfo()
            var data = dat.data as Pair<Room, ArrayList<ByteArray>>
            imgs = data.value
            room = data.key

            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {


                setDetails()

                avDates = getAvailableDates()
                calendarView!!.addDecorators(AvailabilityDecorator(avDates!!))
            }
        }

        calendarView = findViewById<MaterialCalendarView>(R.id.calendar_view)
        calendarView!!.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {

                if(avDates!!.contains(date)) {
                    if (startDate == null) {
                        startDate = date
                    } else if (finishDate == null) {
                        finishDate = date
                        // Optionally sort dates if needed
                        if (startDate!!.isAfter(finishDate!!)) {
                            val temp = startDate
                            startDate = finishDate
                            startDate = temp
                        }
                        highlightSelectedRange()
                    } else {
                        // Reset the selection
                        startDate = date
                        finishDate = null
                        calendarView!!.clearSelection()
                        calendarView!!.setDateSelected(date, true)
                    }
                }
            }
        })

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(room!!.lat, room!!.lon) // Replace with the actual location
        googleMap.addMarker(MarkerOptions().position(location).title("Room Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    /**
     * Gets the available dates of the room and
     * transforms them to CalendarDay
     *
     * @return a HashSet of CalendarDay Objects
     */
    private fun getAvailableDates(): HashSet<CalendarDay> {
        // Example of available dates
        val dates = HashSet<CalendarDay>()
        val d = room!!.availableDates
        for (dat in d){
            dates.add(CalendarDay.from(dat.year,dat.month,dat.dayOfMonth))
        }
        return dates
    }

    /**
     * Sets the details of the room to the screen
     */
    private fun setDetails(){
        val price = findViewById<TextView>(R.id.price_value)
        val name = findViewById<TextView>(R.id.room_name)
        val location = findViewById<TextView>(R.id.location)
        val noOfPeople = findViewById<TextView>(R.id.max_visitors)
        val noOfRooms = findViewById<TextView>(R.id.num_rooms)
        val noOfBaths = findViewById<TextView>(R.id.num_bathrooms)
        val description = findViewById<TextView>(R.id.room_description)

        price.text = room!!.price.toString()
        name.text = room!!.name
        location.text = room!!.area
        noOfPeople.text = room!!.noOfPersons.toString()
        noOfRooms.text = room!!.noOfRooms.toString()
        noOfBaths.text = room!!.noOfBathrooms.toString()
        description.text = room!!.desc

        //for(i in imgs!!){
        var bitmap: Bitmap? = BitmapFactory.decodeByteArray(imgs!![0], 0, imgs!![0].size)
        Log.d("BYTEEEEEEEEE", imgs!![0][0].toString())
        var imgView = findViewById<ImageView>(R.id.img1)
        imgView.setImageBitmap(bitmap)
        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAA", bitmap.toString())
        Log.d("BBBBBBBBBBBBBBBBBBBBBBB", imgs!![0].toString())
        bitmap = BitmapFactory.decodeByteArray(imgs!![1], 0, imgs!![1].size)
        var imgView2 = findViewById<ImageView>(R.id.img2)
        imgView2.setImageBitmap(bitmap)
        bitmap = BitmapFactory.decodeByteArray(imgs!![2], 0, imgs!![2].size)
        var imgView3 = findViewById<ImageView>(R.id.img3)
        imgView3.setImageBitmap(bitmap)
        //}
    }

    /**
     * Highlights the range of dates the user selected
     */
    private fun highlightSelectedRange() {
        if (startDate != null && finishDate != null) {
            val startdate = startDate!!.date.toLocalDate()
            val enddate = finishDate!!.date.toLocalDate()
            val datesInRange = generateDatesInRange(startdate, enddate)

            datesInRange.forEach { date ->
                val calendarDay = CalendarDay.from(date.year, date.monthValue - 1, date.dayOfMonth)
                calendarView!!.setDateSelected(calendarDay, true)
            }
        }
    }

    /**
     * Transforms a date to a local date
     * @return a LocalDate Object
     */
    private fun Date.toLocalDate(): LocalDate {
        return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Generates a list of dates from start date to finish date
     *
     * @param startDate the arrival date
     * @param endDate   the check out date
     * @return a List of localdates
     */
    private fun generateDatesInRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val datesInRange = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            datesInRange.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return datesInRange
    }
}