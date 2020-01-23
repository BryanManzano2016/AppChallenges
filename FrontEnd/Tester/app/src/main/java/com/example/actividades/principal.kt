package com.example.actividades

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clases.Auxiliar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.security.Principal
import java.util.*


@Suppress("DEPRECATION")

class principal : AppCompatActivity() {

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

    private fun remplazarfragmento(fragment: Fragment){
        val fragmentTransition= supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragmentoContenedor,fragment)
        fragmentTransition.commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        inicializarComponentesGUI()
        inicializarEventos()

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun estaActividad(): principal {
        return this
    }

    fun inicializarEventos() {
        /*
        contenedoresImagenes().run { forEach( it.setOnClickListener {
                    val intent = Intent(this, info_challenge::class.java)
                    var arregloEnviar = arrayOf("12")
                    intent.putExtra("arreglo", arregloEnviar)
                    startActivity(intent)
                }
            )
        }
        */
    }


    fun inicializarComponentesGUI() {
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
                    val listaChallenge = LinkedList<com.example.clases.Challenge>()
                    val urls = LinkedList<String>()
                    val nombres = LinkedList<String>()
                    for (i in 0 until solicitud.length()) {
                        listaChallenge.add(Auxiliar().objectoChallenge(solicitud.getJSONObject(i)))
                        urls.add(listaChallenge.last.url)
                        nombres.add(listaChallenge.last.nombre)
                    }
                    Auxiliar().colocarImagen(challenge2, urls.first, editText6, nombres.first)
                    Auxiliar().colocarImagen(challenge, urls.get(1), editText5, nombres.get(1))
                    Auxiliar().colocarImagen(challenge4, urls.get(2), editText4, nombres.get(2))
                }
            }
        }
    }

    private suspend fun CargarChallengesDestacados() {
        val solicitud = obtenerChallengesDestacados()
        val tableRow = TableRow(this)

        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("0")
                -1 -> {
                    val listaChallenge = LinkedList<com.example.clases.Challenge>()
                    for (i in 0 until solicitud.length()) {
                        listaChallenge.add(Auxiliar().objectoChallenge(solicitud.getJSONObject(i)))
                        println(listaChallenge.last.toString())
                    }

                    tablaDestacados.setColumnStretchable(1,true)

                    listaChallenge.forEach {
                        val img = ImageButton(this@principal)
                        Picasso.get()
                                .load(it.url)
                                .resize(350, 250)
                                .centerCrop()
                                .into(img)
                        tablaDestacados.addView(Auxiliar().retornarFilaTabla(img, estaActividad()))
                        val arregloEnviar = arrayOf(it.code_challenges)
                        img.setOnClickListener {
                            val intent = Intent(this@principal, info_challenge::class.java)
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

    // OBTENER CHALLENGES POR CATEGORIA
    private suspend fun obtenerChallengesCategoriaCt(cat : String) {
        val resultadoSolicitud = obtenerChallengesCategoria(cat)
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

    private suspend fun obtenerChallengesCategoria(cat:String ): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "obtenerChallengesCategoria",
                    "{\"categoria\":\""+cat+"\"\"}")
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


}
