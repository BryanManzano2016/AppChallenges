package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.ImageButton
import com.example.clases.Auxiliar
import com.example.clases.Challenge
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_frag_busqueda.*
import kotlinx.android.synthetic.main.activity_principal.bottom_navigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*
import android.widget.LinearLayout
import androidx.core.view.children


class busqueda : AppCompatActivity() {
    var listaCheckBox=LinkedList<CheckBox>()
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
               //ya estas en la busqueda
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
            //onClicked(checkBoxB)

        }
        inicializarCheckBox()
    }

    fun inicializarCheckBox(){
        checkBoxA.text="Ciencia"
        checkBoxB.text="Desarrollo y Tecnologia"
        checkBoxC.text="Ecología"
        checkBoxD.text="Escritura"
        checkBoxE.text="Robotica"
        checkBoxF.text="Salud"
        // Unicamente existe 6 categorias por defecto, el administrador es el encargado de unir categorias si es que lo hay
        listaCheckBox.add(checkBoxA)
        listaCheckBox.add(checkBoxB)
        listaCheckBox.add(checkBoxC)
        listaCheckBox.add(checkBoxD)
        listaCheckBox.add(checkBoxE)
        listaCheckBox.add(checkBoxF)

    }



    private suspend fun onClicked(checkB: CheckBox){
        checkB.setOnClickListener {
             GlobalScope.launch {
                 obtenerChallengesCategoriaCt(checkB.text.toString())
             }
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


                    tableLayout2.setColumnStretchable(1,true)

                    listaBusqueda.forEach {
                        val img= ImageButton(this@busqueda)
                        val lineal= LinearLayout(this@busqueda)
                        lineal.setHorizontalGravity(2)
                        lineal.setVerticalGravity(10)
                        Picasso.get()
                                .load(it.url)
                                .resize(250, 150)
                                .centerCrop()
                                .into(img)
                            lineal.addView(img)
                            tableLayout2.addView(lineal)
                            val arregloEnviar = arrayOf(it.code_challenges)
                            img.setOnClickListener {
                            val intent = Intent(this@busqueda, info_challenge::class.java)

                            intent.putExtra("arreglo", arregloEnviar)
                            startActivity(intent)
                        }

                        }

                    }

                }
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
                    tableLayout2.setColumnStretchable(1,true)
                    tableLayout2.removeAllViews()
                    listaChallenge.forEach {
                        val img= ImageButton(this@busqueda)
                        val lineal= LinearLayout(this@busqueda)
                        lineal.setHorizontalGravity(2)
                        lineal.setVerticalGravity(10)
                        Picasso.get()
                                .load(it.url)
                                .resize(250, 150)
                                .centerCrop()
                                .into(img)
                        lineal.addView(img)
                        tableLayout2.addView(lineal)
                        val arregloEnviar = arrayOf(it.code_challenges)
                        img.setOnClickListener {
                            val intent = android.content.Intent(this@busqueda, com.example.actividades.info_challenge::class.java)

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

    private suspend fun obtenerChallengesCategoria(cat:String ): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "obtenerChallengesCategoria",
                    "{\"categoria\":$cat}"
            )
            return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
        }
    }


}
