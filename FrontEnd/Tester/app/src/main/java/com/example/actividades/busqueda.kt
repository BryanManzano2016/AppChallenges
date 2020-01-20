package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableRow
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.clases.Auxiliar
import com.example.clases.Challenge
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_frag_busqueda.*
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.activity_principal.bottom_navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*
import kotlin.collections.HashMap
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.TableLayout
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.LinearLayout


class busqueda : AppCompatActivity() {
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, principal::class.java)
                startActivity(intent)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frag_busqueda)
        empezar()
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun empezar(){
        GlobalScope.launch {
            CargarChallengesBusquedaCt()
        }
    }


    private suspend fun CargarChallengesBusquedaCt() {
        val solicitud = obtenerChallengesBusqueda()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("0")
                -1 -> {
                    val listaBusqueda = LinkedList<Challenge>()
                    for (i in 0 until solicitud.length()) {
                        listaBusqueda.add(Auxiliar().objectoChallenge(solicitud.getJSONObject(i)))
                    }


                    tableLayout.setColumnStretchable(1,true)

                    listaBusqueda.forEach {
                        val img= ImageButton(this@busqueda)
                        val lineal= LinearLayout(this@busqueda)
                        Picasso.get()
                                .load(it.url)
                                .resize(250, 150)
                                .centerCrop()
                                .into(img)
                            lineal.addView(img)
                            tableLayout.addView(lineal)
                            val arregloEnviar = arrayOf(it.code_challenges)
                            img.setOnClickListener {
                            val intent = Intent(this@busqueda, contr_grupoxchalenge::class.java)

                            intent.putExtra("arreglo", arregloEnviar)
                            startActivity(intent)

                        }
                        }

                    }

                }
            }
        }


    private suspend fun obtenerChallengesBusqueda(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpGet(
                    Auxiliar().obtener_Ip() + "obtenerChallenges"

            )

            val respuesta = Auxiliar().respuestaString(solicitud.body())
            return@withContext JSONArray(respuesta)
        }
    }
}
