package com.example.layout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.actividades.R
import kotlinx.android.synthetic.main.activity_info_challenge.*
import kotlinx.android.synthetic.main.activity_principal.*

class info_challenge : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_challenge)
        boton()

    }

    fun boton(){
        button2.setOnClickListener {
            val intent = Intent(this, unidochallenge::class.java)
            var arregloEnviar = arrayOf("2")
            intent.putExtra("arreglo", arregloEnviar)
            startActivity(intent)
        }
    }
}
