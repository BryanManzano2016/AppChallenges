package com.example.actividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_tusgrupos.*

class tusgrupos : AdapterView.OnItemSelectedListener,AppCompatActivity() {

    var list_of_items=arrayOf("Challenge1","Challenge2","Challenge3")
    var grupos=arrayOf("grupo1","grupo2","grupo3")
    var spinner:Spinner? = null
    var textView_msg:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tusgrupos)


        spinner = this.spinner_sample
        spinner!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)

    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        if(list_of_items[position].equals("Challenge2")) {
            lateinit var lViewgrupos: ListView
            lViewgrupos = findViewById(R.id.lViewgrupos)
            val adaptador1 = ArrayAdapter<String>(this@tusgrupos, R.menu.list_item_grupos, grupos)
            lViewgrupos.adapter = adaptador1
            lViewgrupos.setOnItemClickListener { adapterView, view, i, l ->
                setContentView(R.layout.activity_pall_infogrupo)
            }
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}

