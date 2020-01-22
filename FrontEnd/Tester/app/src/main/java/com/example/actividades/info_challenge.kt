package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_info_challenge.*

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
