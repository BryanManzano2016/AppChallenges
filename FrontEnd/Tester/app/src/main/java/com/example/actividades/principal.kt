package com.example.actividades

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_principal.*


class principal : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
            when (item.itemId) {
        R.id.nav_home -> { return@OnNavigationItemSelectedListener true }
        R.id.nav_favorites ->{ return@OnNavigationItemSelectedListener true }
        R.id.nav_search -> { return@OnNavigationItemSelectedListener true }
        }
        false

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
