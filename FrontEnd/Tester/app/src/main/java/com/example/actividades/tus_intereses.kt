package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_principal.*

class tus_intereses : AppCompatActivity() {
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, principal::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true }
            R.id.nav_favorites ->{
                val intent = Intent(this, tus_intereses::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true }
            R.id.nav_search -> {
                //remplazarfragmento(fragmento_busqueda())
                return@OnNavigationItemSelectedListener true }
        }
        false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frag_intereses)
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
