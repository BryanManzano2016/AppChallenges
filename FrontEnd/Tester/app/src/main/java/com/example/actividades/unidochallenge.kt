package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_unidochallenge.*

class unidochallenge : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidochallenge)

        inicializarComponentesGUI()
        iniciarEventos()
    }

    fun iniciarEventos(){
        val arregloRecibido = intent?.getStringArrayExtra("arreglo")
        irGrupos.setOnClickListener {
            val intent = Intent(this, contr_grupoxchalenge::class.java)
            println("*********** ${arregloRecibido?.get(0)}")
            var arregloEnviar = arrayOf(arregloRecibido?.get(0))
            intent.putExtra("arreglo", arregloEnviar)
            startActivity(intent)
        }
    }

    fun inicializarComponentesGUI() {

    }
}
