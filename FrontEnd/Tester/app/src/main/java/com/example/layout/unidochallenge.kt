package com.example.layout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.actividades.R
import com.example.actividades.contr_grupoxchalenge
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.activity_unidochallenge.*

class unidochallenge : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidochallenge)
        imagen()
    }

    fun imagen(){
        imageView2.setOnClickListener {
            val intent = Intent(this, contr_grupoxchalenge::class.java)
            var arregloEnviar = arrayOf("2")
            intent.putExtra("arreglo", arregloEnviar)
            startActivity(intent)
        }
    }
}
