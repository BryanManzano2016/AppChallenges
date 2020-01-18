package com.example.actividades

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clases.Auxiliar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONArray
import java.util.*

class Inicio : AppCompatActivity(), CoroutineScope by MainScope() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarComponentesGUI()
        inicializarEventos()
    }

    fun inicializarComponentesGUI(){
        // code
    }

    fun inicializarEventos(){
        botonObtenerChallenges.setOnClickListener {
            // lanzar corutina
            launch {
                obtenerChallengesCt()
            }
        }
        botonObtenerChallengesCategoria.setOnClickListener {
            // lanzar corutina
            launch {
                obtenerChallengesCategoriaCt()
            }
        }
        botonMeEcanta.setOnClickListener {
            // lanzar corutina
            launch {
                meEncantaCt()
            }
        }
        botonObteneventanainfogrupo.setOnClickListener {

            val intent = Intent(this, contr_grupoxchalenge::class.java)
            startActivity(intent)

        }
    }
    // OBTENER CHALLENGES
    private suspend fun obtenerChallengesCt() {
        val resultadoSolicitud = obtenerChallenges()
        withContext(Dispatchers.Main) {
            when( Auxiliar().mensajeServidor(resultadoSolicitud) ) {
                0 -> texto.text = "0"
                -1 -> {
                    val listaChallenge = LinkedList<com.example.clases.Challenge>()
                    for (i in 0 until resultadoSolicitud.length()) {
                        listaChallenge.add( Auxiliar().objectoChallenge( resultadoSolicitud.getJSONObject(i) ) )
                    }
                    var cadena = ""
                    listaChallenge.forEach { cadena += it.nombre + "\n" }
                    texto.text = cadena
                }
            }
        }
    }
    private suspend fun obtenerChallenges(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpGet(
                    Auxiliar().obtener_Ip()+"obtenerChallenges"

            )
            val respuesta = Auxiliar().respuestaString( solicitud.body() )
            return@withContext JSONArray( respuesta )
        }
    }

    // OBTENER CHALLENGES POR CATEGORIA
    private suspend fun obtenerChallengesCategoriaCt() {
        val resultadoSolicitud = obtenerChallengesCategoria()
        withContext(Dispatchers.Main) {
            when( Auxiliar().mensajeServidor(resultadoSolicitud) ) {
                0 -> texto.text = "0"
                -1 -> {
                    val listaChallenge = LinkedList<com.example.clases.Challenge>()
                    for (i in 0 until resultadoSolicitud.length()) {
                        listaChallenge.add( Auxiliar().objectoChallenge( resultadoSolicitud.getJSONObject(i) ) )
                    }
                    var cadena = ""
                    listaChallenge.forEach { cadena += it.nombre + "\n" }
                    texto.text = cadena
                }
            }
        }
    }
    private suspend fun obtenerChallengesCategoria(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip()+"obtenerChallengesCategoria",
                    "{\"categoria\":\"Ciencia\"}" )
            return@withContext JSONArray( Auxiliar().respuestaString( solicitud.body() ) )
        }
    }

    // DAR ME GUSTA
    private suspend fun meEncantaCt() {
        val resultadoSolicitud = meEncanta()
        withContext(Dispatchers.Main) {
            when( Auxiliar().mensajeServidor(resultadoSolicitud) ) {
                0 -> texto.text = "0"
                1 -> texto.text = "1"
                2 -> texto.text = "2"
            }
        }
    }
    private suspend fun meEncanta(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip()+"meEncantaChallenge",
                    "{\"idChallenge\":3}" )
            return@withContext JSONArray( Auxiliar().respuestaString( solicitud.body() ) )
        }
    }
}

