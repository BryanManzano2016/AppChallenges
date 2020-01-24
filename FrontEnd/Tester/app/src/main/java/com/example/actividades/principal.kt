package com.example.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clases.Auxiliar
import com.example.clases.Challenge
import com.example.clases.Confirmacion
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.sql.SQLOutput
import java.util.*


@Suppress("DEPRECATION")

class principal : AppCompatActivity() {

    private var imagenesRecientes = LinkedList<ImageButton>()
    private var imagenesDestacados = LinkedList<ImageButton>()
    private var textosCR = LinkedList<TextView>()
    private var textosCD = LinkedList<TextView>()
    private lateinit var challengeSelecc: Challenge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        inicializarComponentesGUI()

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onResume() {
        super.onResume()
        inicializarComponentesGUI()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView
            .OnNavigationItemSelectedListener { item->
        when (item.itemId) {
            R.id.nav_home -> {
                return@OnNavigationItemSelectedListener true }
            R.id.nav_favorites ->{
                val intent = Intent(this, tus_intereses::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true }
            R.id.nav_search -> {
                val intent = Intent(this, busqueda::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true }
            }
        false
    }

    fun estaActividad(): principal {
        return this
    }

    fun inicializarComponentesGUI() {
        imagenesDestacados.clear()
        imagenesRecientes.clear()
        textosCR.clear()
        imagenesDestacados.add(challenge1)
        imagenesDestacados.add(challenge2)
        imagenesDestacados.add(challenge3)
        imagenesRecientes.add(challenge4)
        imagenesRecientes.add(challenge5)
        imagenesRecientes.add(challenge6)
        textosCD.add(textC1)
        textosCD.add(textC2)
        textosCD.add(textC3)
        textosCR.add(textC4)
        textosCR.add(textC5)
        textosCR.add(textC6)
        GlobalScope.launch {
            CargarChallengesDestacados()
            CargarChallengesRecientes()
        }
    }

    private suspend fun CargarChallengesRecientes() {
        val solicitud = obtenerChallengesRecientes()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("0")
                -1 -> {
                    for (i in 0 until solicitud.length()) {
                        val challenge = Auxiliar().objectoChallenge(solicitud.getJSONObject(i))
                        Auxiliar().colocarImagen(imagenesRecientes[i], challenge.url,
                                textosCR[i], challenge.nombre)
                        imagenesRecientes[i].setOnClickListener {
                            val intent = Intent(estaActividad(), info_challenge::class.java)
                            val arregloEnviar = arrayOf(challenge.code_challenges)
                            intent.putExtra("arreglo", arregloEnviar)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private suspend fun CargarChallengesDestacados() {
        val solicitud = obtenerChallengesDestacados()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("0")
                -1 -> {
                    for (i in 0 until solicitud.length()) {
                        val challenge = Auxiliar().objectoChallenge(solicitud.getJSONObject(i))
                        Auxiliar().colocarImagen(imagenesDestacados[i], challenge.url,
                                textosCD[i], challenge.nombre)
                        imagenesDestacados[i].setOnClickListener {
                            val intent = Intent(estaActividad(), info_challenge::class.java)
                            val arregloEnviar = arrayOf(challenge.code_challenges)
                            intent.putExtra("arreglo", arregloEnviar)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private suspend fun obtenerChallengesDestacados(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpGet(
                    Auxiliar().obtener_Ip() + "obtenerChallengesDestacados"
            )
            val respuesta = Auxiliar().respuestaString(solicitud.body())
            return@withContext JSONArray(respuesta)
        }
    }

    private suspend fun obtenerChallengesRecientes(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpGet(
                    Auxiliar().obtener_Ip() + "obtenerChallengesRecientes"
            )
            val respuesta = Auxiliar().respuestaString(solicitud.body())
            return@withContext JSONArray(respuesta)
        }
    }








}
