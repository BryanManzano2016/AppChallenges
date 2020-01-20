package com.example.actividades
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.layout.info_challenge
import kotlinx.android.synthetic.main.activity_pall_infogrupo.*
import kotlinx.android.synthetic.main.activity_unidochallenge.*


class controlador_pall_infogrupo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pall_infogrupo)
        ir()

    }

    fun ir(){
        botonagregar.setOnClickListener {
            val intent = Intent(this, controlador_pall_infogrupo2::class.java)
            var arregloEnviar = arrayOf("2")
            intent.putExtra("arreglo", arregloEnviar)
            startActivity(intent)
        }
    }
}
