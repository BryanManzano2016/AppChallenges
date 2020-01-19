package com.example.actividades

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clases.Auxiliar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONArray
import java.util.*
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.bottom_navigation
import kotlinx.android.synthetic.main.activity_principal.*
import java.text.SimpleDateFormat
import java.time.Month

@Suppress("DEPRECATION")
class Inicio : AppCompatActivity(), CoroutineScope by MainScope() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        inicializarComponentesGUI()
        inicializarEventos()

    }

    fun inicializarComponentesGUI() {
        // code
    }


    fun inicializarEventos() {
        GlobalScope.launch {
            CargarChallengesDestacados()
            CargarChallengesRecientes()
        }


    }
    // OBTENER CHALLENGES


    private suspend fun CargarChallengesRecientes() {
        val solicitud = obtenerChallenges()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("0")
                -1 -> {
                    val listaChallenge = LinkedList<com.example.clases.Challenge>()
                    val urls = LinkedList<String>()
                    val nombres = LinkedList<String>()
                    for (i in 0 until solicitud.length()) {
                        listaChallenge.add(Auxiliar().objectoChallenge(solicitud.getJSONObject(i)))

                    }
                    listaChallenge.forEach {
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val strDate = sdf.parse(it.fechaInicio)
                        val strCom = sdf.parse("2019-12-15")
                        if (Date().after(strDate) and strCom.before(strDate)) {
                            print("***********************************************")
                            print(strDate.month)
                            urls.add(it.url)
                            nombres.add(it.nombre)
                        }
                    }
                    Auxiliar().colocarImagen(challenge2, urls.first, editText6, nombres.first)
                    Auxiliar().colocarImagen(challenge, urls.get(1), editText5, nombres.get(1))
                    Auxiliar().colocarImagen(challenge4, urls.get(2), editText4, nombres.get(2))
                }
            }
        }
    }


    private suspend fun CargarChallengesDestacados() {
        val solicitud = obtenerChallenges()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("0")
                -1 -> {
                    val listaChallenge = LinkedList<com.example.clases.Challenge>()
                    val urls = LinkedList<String>()
                    val nombres = LinkedList<String>()
                    for (i in 0 until solicitud.length()) {
                        listaChallenge.add(Auxiliar().objectoChallenge(solicitud.getJSONObject(i)))

                    }
                    listaChallenge.forEach {
                        urls.add(it.url)
                        nombres.add(it.nombre)
                    }
                    Auxiliar().colocarImagen(Challenge1, urls.first, editText, nombres.first)
                    Auxiliar().colocarImagen(challenge3, urls.get(1), editText2, nombres.get(1))
                    Auxiliar().colocarImagen(challenge5, urls.get(2), editText3, nombres.get(2))

                }
            }
        }
    }

    private suspend fun obtenerChallenges(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpGet(
                    Auxiliar().obtener_Ip() + "obtenerChallenges"

            )
            val respuesta = Auxiliar().respuestaString(solicitud.body())
            return@withContext JSONArray(respuesta)
        }
    }

    // OBTENER CHALLENGES POR CATEGORIA
    private suspend fun obtenerChallengesCategoriaCt() {
        val resultadoSolicitud = obtenerChallengesCategoria()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(resultadoSolicitud)) {
                0 -> print("0")
                -1 -> {
                    val listaChallenge = LinkedList<com.example.clases.Challenge>()
                    for (i in 0 until resultadoSolicitud.length()) {
                        listaChallenge.add(Auxiliar().objectoChallenge(resultadoSolicitud.getJSONObject(i)))
                    }
                    var cadena = ""
                    listaChallenge.forEach { cadena += it.nombre + "\n" }
                    print(cadena)
                }
            }
        }
    }

    private suspend fun obtenerChallengesCategoria(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "obtenerChallengesCategoria",
                    "{\"categoria\":\"Ciencia\"}")
            return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
        }
    }

    // DAR ME GUSTA
    private suspend fun meEncantaCt() {
        val resultadoSolicitud = meEncanta()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(resultadoSolicitud)) {
                0 -> print("0")
                1 -> print("1")
                2 -> print("2")
            }
        }
    }

    private suspend fun meEncanta(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "meEncantaChallenge",
                    "{\"idChallenge\":3}")
            return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
        }
    }

    private fun remplazarfragmento(fragment: Fragment){
        val fragmentTransition= supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragmentoContenedor,fragment)
        fragmentTransition.commit()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when (item.itemId) {
            R.id.nav_home -> {
                remplazarfragmento(fragmento_home())
                return@OnNavigationItemSelectedListener true }
            R.id.nav_favorites ->{
                remplazarfragmento(fragmento_busqueda())
                return@OnNavigationItemSelectedListener true }
            R.id.nav_search -> {
                remplazarfragmento(fragmento_intereses())
                return@OnNavigationItemSelectedListener true }
        }
        false

    }
}

