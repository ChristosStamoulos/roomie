package com.example.roomie.frontend

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
    var whenbtn: TextView? = null
    var whobtn: TextView? = null
    var when_expanded: MaterialCalendarView? = null
    var who_expanded: LinearLayout? = null

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
        whobtn = findViewById<TextView>(R.id.whobtn) as TextView?
        who_expanded = findViewById(R.id.who_expanded)
        whobtn!!.setOnClickListener {
            if (who_expanded!!.visibility == View.VISIBLE){
                who_expanded!!.visibility = View.GONE
            }else{
                who_expanded!!.visibility = View.VISIBLE
            }
        }

        whenbtn = findViewById(R.id.whenbtn)
        when_expanded = findViewById(R.id.when_expanded)
        whenbtn!!.setOnClickListener {
            if (when_expanded!!.visibility == View.VISIBLE){
                when_expanded!!.visibility = View.GONE
            }else{
                when_expanded!!.visibility = View.VISIBLE
            }
        }
    }
}