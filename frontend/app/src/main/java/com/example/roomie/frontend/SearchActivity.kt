package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.roomie.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    var bottomNavigationView: BottomNavigationView? = null
    var whenbtn: LinearLayout? = null
    var whobtn: LinearLayout? = null
    var wherebtn: LinearLayout? = null
    var pricebtn: LinearLayout? = null
    var starsbtn: LinearLayout? = null
    var when_expanded: LinearLayout? = null
    var who_expanded: LinearLayout? = null
    var where_expanded: LinearLayout? = null
    var stars_expanded: LinearLayout? = null
    var price_expanded: LinearLayout? = null
    var calendarView: MaterialCalendarView? = null
    var location: String? = null
    var people = 0
    private var startDate: CalendarDay? = null
    private var finishDate: CalendarDay? = null
    var minPrice = 0
    var maxPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        bottomNavigationView = findViewById<View>(R.id.bottomNav) as BottomNavigationView?

        bottomNavigationView?.setSelectedItemId(R.id.searchbtn)
        bottomNavigationView!!.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId
            if (item.itemId == R.id.accountbtn) {
                val intent: Intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
            } else if (itemId == R.id.reservbtn) {
                val intent: Intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
            } else if (itemId == R.id.searchbtn) {
                val intent: Intent = Intent(this, SearchActivity::class.java)
            } else if (itemId == R.id.homeBtn) {
                val intent: Intent = Intent(this, HomeScreenActivity::class.java)
                val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_out ,  // Animation for the entering activity
                        R.anim.slide_in// Animation for the exiting activity
                )
                startActivity(intent, options.toBundle())
            }
            item.setChecked(true)
            true
        })
        whoButton()
        whenButton()
        whereButton()
        priceButton()
        starsButton()
    }

    fun whereButton(){
        wherebtn = findViewById(R.id.wherebtn)
        where_expanded = findViewById(R.id.where_expanded)
        wherebtn!!.setOnClickListener {
            if (where_expanded!!.visibility == View.VISIBLE){
                where_expanded!!.visibility = View.GONE
                wherebtn!!.visibility = View.VISIBLE
            }else{
                where_expanded!!.visibility = View.VISIBLE
                wherebtn!!.visibility = View.GONE
            }
        }
        var skipWhere = findViewById<Button>(R.id.skipWhere)
        skipWhere.setOnClickListener{
            where_expanded!!.visibility = View.GONE
            wherebtn!!.visibility = View.VISIBLE
            when_expanded!!.visibility = View.VISIBLE
            whenbtn!!.visibility = View.GONE
        }

        var nextWhere = findViewById<Button>(R.id.nextWhere)
        var placetxt = findViewById<TextView>(R.id.wheretxt)
        var placeQuery = findViewById<EditText>(R.id.placeQuery)
        nextWhere.setOnClickListener {
            if (!placeQuery.equals("")){
                placetxt.text = placeQuery.text.toString()
                location = placeQuery.text.toString()
            }
            //TO DO: add message that place needs to be filled
            where_expanded!!.visibility = View.GONE
            wherebtn!!.visibility = View.VISIBLE
            when_expanded!!.visibility = View.VISIBLE
            whenbtn!!.visibility = View.GONE
        }
    }

    fun whenButton(){
        whenbtn = findViewById(R.id.whenbtn)
        when_expanded = findViewById(R.id.when_expanded)
        whenbtn!!.setOnClickListener {
            if (when_expanded!!.visibility == View.VISIBLE){
                when_expanded!!.visibility = View.GONE
                whenbtn!!.visibility = View.VISIBLE
            }else{
                when_expanded!!.visibility = View.VISIBLE
                whenbtn!!.visibility = View.GONE
            }
        }
        var skipWhen = findViewById<Button>(R.id.skipWhen)
        skipWhen.setOnClickListener{
            when_expanded!!.visibility = View.GONE
            whenbtn!!.visibility = View.VISIBLE
            who_expanded!!.visibility = View.VISIBLE
            whobtn!!.visibility = View.GONE
        }

        var nextWhen = findViewById<Button>(R.id.nextWhen)
        nextWhen.setOnClickListener {
            //TO DO: add clickable dates
            when_expanded!!.visibility = View.GONE
            whenbtn!!.visibility = View.VISIBLE
            who_expanded!!.visibility = View.VISIBLE
            whobtn!!.visibility = View.GONE
        }

        calendarView = findViewById(R.id.when_calendar)


        calendarView!!.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
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
        })

    }

    fun whoButton(){
        whobtn = findViewById(R.id.whobtn)
        who_expanded = findViewById(R.id.who_expanded)
        whobtn!!.setOnClickListener {
            if (who_expanded!!.visibility == View.VISIBLE){
                who_expanded!!.visibility = View.GONE
                whobtn!!.visibility = View.VISIBLE
            }else{
                who_expanded!!.visibility = View.VISIBLE
                whobtn!!.visibility = View.GONE
            }
        }

        var skipWho = findViewById<Button>(R.id.skipWho)
        skipWho.setOnClickListener{
            who_expanded!!.visibility = View.GONE
            whobtn!!.visibility = View.VISIBLE
            //stars_expanded!!.visibility = View.VISIBLE
            //starsbtn!!.visibility = View.GONE
        }

        var nextWho = findViewById<Button>(R.id.nextWhen)
        var ppltxt = findViewById<TextView>(R.id.ppltxt)
        var whotxt = findViewById<TextView>(R.id.whotxt)
        nextWho.setOnClickListener {
            //TO DO:
            who_expanded!!.visibility = View.GONE
            whobtn!!.visibility = View.VISIBLE
            stars_expanded!!.visibility = View.VISIBLE
            ppltxt.text = people.toString()
            whotxt.text = people.toString()
        }

        var plusbtn = findViewById<Button>(R.id.plusppl)
        var ppl = findViewById<TextView>(R.id.minppl)
        plusbtn.setOnClickListener {
            people +=1
            ppl.text = people.toString()
        }

        var minusppl = findViewById<Button>(R.id.minusppl)
        minusppl.setOnClickListener {
            if(people > 0){
                people -=1
                ppl.text = people.toString()
            }
        }
    }

    fun priceButton(){
        pricebtn = findViewById(R.id.pricebtn)
        price_expanded = findViewById(R.id.price_expanded)
        pricebtn!!.setOnClickListener {
            if (price_expanded!!.visibility == View.VISIBLE){
                price_expanded!!.visibility = View.GONE
                pricebtn!!.visibility = View.VISIBLE
            }else{
                price_expanded!!.visibility = View.VISIBLE
                pricebtn!!.visibility = View.GONE
            }
        }

        var skipPrice = findViewById<Button>(R.id.skipPrice)
        skipPrice.setOnClickListener{
            pricebtn!!.visibility = View.VISIBLE
            price_expanded!!.visibility = View.GONE
        }

        var nextPrice = findViewById<Button>(R.id.nextPrice)
        nextPrice.setOnClickListener {
            //TO DO:
            who_expanded!!.visibility = View.GONE
            whobtn!!.visibility = View.VISIBLE
            pricebtn!!.visibility = View.VISIBLE
            price_expanded!!.visibility = View.GONE
            val ptxt = findViewById<TextView>(R.id.pricetxt)
            ptxt.text = minPrice.toString()

        }

        //TO DO: price range for max

        val priceSeekBar = findViewById<SeekBar>(R.id.priceBar)

        // Set the minimum and maximum values for the SeekBar (e.g., price range)
        val minP = 0
        val maxP = 1000
        priceSeekBar.min = minP
        priceSeekBar.max = maxP
        var price = 0

        // Set up a listener to handle changes in the SeekBar progress
        priceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                price = (minP + (maxP - minP) * (progress.toFloat() / priceSeekBar.max)).toInt()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Handle when the user starts dragging the SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                minPrice = price
            }
        })
    }

    fun starsButton(){

    }

    fun createJson() : JSONObject{
        var filters = JSONObject()
        var filter = JSONObject()
        if (location.equals("")){
            filter.put("area", "default")
        }else{
            filter.put("area", location)
        }

        if(startDate!=null){
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
            val d = startDate!!.date.toLocalDate().format(formatter)
            filter.put("startDate", d)
        }else{
            filter.put("startDate", "01/01/0001")
        }

        if(finishDate!=null){
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
            val d = finishDate!!.date.toLocalDate().format(formatter)
            filter.put("finishDate", d)
        }else{
            filter.put("finishDate", "01/01/0001")
        }

        filter.put("noOfPeople", people)

        filter.put("lowPrice", 0)
        filter.put("highPrice", 0)
        filter.put("stars", 0.0)

        filters.put("filters", filter)
        return filters
    }

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

    private fun Date.toLocalDate(): LocalDate {
        return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }


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