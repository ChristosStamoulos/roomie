package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.roomie.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

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
    var location: String? = null
    var people = 0

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
            where_expanded!!.visibility = View.VISIBLE
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
            price_expanded!!.visibility = View.GONE
            pricebtn!!.visibility = View.VISIBLE
        }

        var nextPrice = findViewById<Button>(R.id.nextPrice)
        nextPrice.setOnClickListener {
            //TO DO:
            who_expanded!!.visibility = View.GONE
            whobtn!!.visibility = View.VISIBLE
        }

        //TO DO: price range
    }

    fun starsButton(){

    }
}