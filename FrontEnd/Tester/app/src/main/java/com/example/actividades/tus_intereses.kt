package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.clases.Auxiliar
import com.example.clases.Challenge
import com.example.clases.Grupos
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_frag_busqueda.*
import kotlinx.android.synthetic.main.activity_frag_intereses.*
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.activity_principal.bottom_navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*

class tus_intereses : AppCompatActivity() {
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, principal::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true }
            R.id.nav_favorites ->{
                //ya estas en interese
                return@OnNavigationItemSelectedListener true }
            R.id.nav_search -> {
                val intent = Intent(this,busqueda::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true }
        }
        false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frag_intereses)
        intereses()
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }



    fun intereses(){
        GlobalScope.launch {
            obtenerChallengesInteresadosCt(getString(R.string.idEstudiante).toInt())
        }
    }
    private suspend fun obtenerChallengesInteresadosCt(cat : Int) {
        val solicitud = obtenerChallengesInteresados(cat)
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> println("0")
                -1 -> {
                    val listaInteres = LinkedList<Challenge>()
                    for (i in 0 until solicitud.length()) {
                        listaInteres.add(Auxiliar().objectoChallenge(solicitud.getJSONObject(i)))
                    }

                    tableLayout.setColumnStretchable(1,true)

                    listaInteres.forEach {
                        val img= ImageButton(this@tus_intereses)
                        val lineal= LinearLayout(this@tus_intereses)
                        lineal.setAutofillHints()
                        Picasso.get()
                                .load(it.url)
                                .resize(350, 250)
                                .centerCrop()
                                .into(img)
                        lineal.addView(img)
                        tableLayout.addView(lineal)
                        val arregloEnviar = arrayOf(it.code_challenges)
                        img.setOnClickListener {
                            val intent = android.content.Intent(this@tus_intereses, contr_grupoxchalenge::class.java)
                            intent.putExtra("arreglo", arregloEnviar)
                            startActivity(intent)

                        }
                    }
                }
            }
        }
    }

    private suspend fun obtenerChallengesInteresados(id: Int): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "obtenerChallengesInteresados",
                    "{\"idEstudiante\":$id}"
            )
            return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
        }

    }
}
