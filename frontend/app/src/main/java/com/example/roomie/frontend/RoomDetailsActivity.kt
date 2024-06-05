package com.example.roomie.frontend

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.roomie.R
import com.example.roomie.backend.domain.Chunk
import com.example.roomie.backend.domain.Room
import com.example.roomie.backend.utils.Pair
import com.example.roomie.backend.utils.SimpleCalendar
import com.example.roomie.frontend.utils.AvailabilityDecorator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
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
    lateinit var googleMap: GoogleMap
    var backendCommunicator: BackendCommunicator? = null
    var imgs: ArrayList<ByteArray>? = null
    var room: Room? = null
    var calendarView: MaterialCalendarView? = null
    private var startDate: CalendarDay? = null
    private var finishDate: CalendarDay? = null
    var avDates: HashSet<CalendarDay>? = null
    private var selectedDates: ArrayList<SimpleCalendar> = ArrayList()
    private var selectedDatesToMaster: ArrayList<String> = ArrayList()
    var bottomNavigationView: BottomNavigationView? = null

    @SuppressLint("MissingInflatedId")
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
            Log.e("RoomDetailsActivity", imgs.toString())

            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {

                setDetails()

                avDates = getAvailableDates()
                calendarView!!.addDecorators(AvailabilityDecorator(avDates!!))
                setupMap()
            }
        }

        calendarView = findViewById<MaterialCalendarView>(R.id.calendar_view)
        calendarView!!.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {

                if (avDates!!.contains(date)) {
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

        val bookButton = findViewById<Button>(R.id.book_btn)
        bookButton.setOnClickListener {
            makeBooking()
        }

        bottomNavigationView = findViewById<View>(R.id.bottomNav) as BottomNavigationView?

        bottomNavigationView?.setSelectedItemId(R.id.homeBtn)
        bottomNavigationView!!.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            if (item.itemId == R.id.accountbtn) {
                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
            } else if (itemId == R.id.homeBtn) {
                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
            } else if (itemId == R.id.searchbtn) {
                val intent = Intent(this, SearchActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_in,  // Animation for the entering activity
                        R.anim.slide_out  // Animation for the exiting activity
                )
                startActivity(intent, options.toBundle())
            } else if(itemId == R.id.reservbtn){
                val intent = Intent(this, ReservationsActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_in,  // Animation for the entering activity
                        R.anim.slide_out  // Animation for the exiting activity
                )
                startActivity(intent, options.toBundle())
            }
            item.setChecked(true)
            true
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    private fun setupMap() {
        if (googleMap != null && room != null) {
            val lat = room!!.lat
            val lon = room!!.lon

            // Validate the latitude and longitude values
            if (lat in -90.0..90.0 && lon in -180.0..180.0) {
                val location = LatLng(lat, lon)
                googleMap!!.addMarker(MarkerOptions().position(location).title(room!!.name))
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        }
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
        for (dat in d) {
            dates.add(CalendarDay.from(dat.year, dat.month-1, dat.dayOfMonth))
        }
        return dates
    }

    /**
     * Sets the details of the room to the screen
     */
    private fun setDetails() {
        val price = findViewById<TextView>(R.id.price_value)
        val name = findViewById<TextView>(R.id.room_name)
        val location = findViewById<TextView>(R.id.location)
        val noOfPeople = findViewById<TextView>(R.id.max_visitors)
        val noOfRooms = findViewById<TextView>(R.id.num_rooms)
        val noOfBaths = findViewById<TextView>(R.id.num_bathrooms)
        val description = findViewById<TextView>(R.id.room_description)
        val stars = findViewById<TextView>(R.id.starstxt)

        price.text = room!!.price.toString()
        name.text = room!!.name
        location.text = room!!.area
        noOfPeople.text = room!!.noOfPersons.toString()
        noOfRooms.text = room!!.noOfRooms.toString()
        noOfBaths.text = room!!.noOfBathrooms.toString()
        description.text = room!!.desc
        stars.text = room!!.rating.toString() + " (" + room!!.noOfReviews.toString() + ")"

        //for(i in imgs!!){
        val imgViews = listOf(
                findViewById<ImageView>(R.id.img1),
                findViewById<ImageView>(R.id.img2),
                findViewById<ImageView>(R.id.img3)
        )

        for (i in imgs!!.indices) {
            val imgData = imgs!![i]

            if (imgData.size > 100) {  // Assuming a minimum size for a valid image
                Log.d("RoomDetailsActivity", "Image $i data size: ${imgData.size}")
                val bitmap: Bitmap? = BitmapFactory.decodeByteArray(imgData, 0, imgData.size)

                if (bitmap != null) {
                    imgViews[i].setImageBitmap(bitmap)
                    Log.d("RoomDetailsActivity", "Successfully decoded and set image $i")
                } else {
                    Log.e("RoomDetailsActivity", "Failed to decode image $i")
                }
            } else {
                Log.e("RoomDetailsActivity", "Image $i data size is too small: ${imgData.size}")
            }
        }
        //}
    }

    /**
     * Highlights the range of dates the user selected
     */
    private fun highlightSelectedRange() {
        if (startDate != null && finishDate != null) {
            val startdate = startDate!!.date.toLocalDate()
            val enddate = finishDate!!.date.toLocalDate()
            selectedDates = generateDatesInRange(startdate, enddate)

            selectedDates.forEach { date ->
                val calendarDay = CalendarDay.from(date.year, date.month - 1, date.dayOfMonth)
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
    private fun generateDatesInRange(startDate: LocalDate, endDate: LocalDate): ArrayList<SimpleCalendar> {
        val datesInRange = ArrayList<SimpleCalendar>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            datesInRange.add(SimpleCalendar(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth))
            currentDate = currentDate.plusDays(1)
        }
        return datesInRange
    }

    /**
     * Books the accommodation, with the dates the user selected
     */
    private fun makeBooking() {
        for (i in selectedDates){
            selectedDatesToMaster.add(i.toString())
        }
        if (room == null) {
            Toast.makeText(this, "Room details not available", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDatesToMaster.isEmpty()) {
            Toast.makeText(this, "Please select dates", Toast.LENGTH_SHORT).show()
            return
        }

        val booking = Pair(room!!.id, selectedDatesToMaster)

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val chunk = Chunk("1", 2, booking)
                Log.d("RoomDetailsActivity", booking.toString())
                val backendCommunicator = BackendCommunicator()
                backendCommunicator.sendMasterInfo(chunk)
                val response = backendCommunicator.sendClientInfo()

                withContext(Dispatchers.Main) {
                    Log.d("RoomDetailsActivity", response?.data.toString())
                    if (response?.data == true) {
                        Toast.makeText(this@RoomDetailsActivity, "Booking successful", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@RoomDetailsActivity, "Booking failed", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RoomDetailsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}