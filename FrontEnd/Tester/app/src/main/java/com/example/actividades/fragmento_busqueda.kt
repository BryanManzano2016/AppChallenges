package com.example.actividades


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView

import com.example.actividades.R
import com.example.clases.Auxiliar
import com.example.clases.Challenge
import kotlinx.android.synthetic.main.activity_frag_busqueda.*
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class fragmento_busqueda : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_frag_busqueda, container, false)
    }


    private suspend fun CargarChallengesDestacados() {
        val solicitud = obtenerChallengesBusqueda()
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("0")
                -1 -> {
                    val listaChallenge = LinkedList<Challenge>()
                    val urls = LinkedList<String>()
                    val nombres = LinkedList<String>()
                    val mapa=HashMap<ImageButton,Challenge>()
                    for (i in 0 until solicitud.length()) {
                        listaChallenge.add(Auxiliar().objectoChallenge(solicitud.getJSONObject(i)))
                    }

                    mapa.put(imageButton2,listaChallenge[0])
                    
                    Auxiliar().colocarImagen(Challenge1, urls.first, editText, nombres.first)
                    Auxiliar().colocarImagen(challenge3, urls.get(1), editText2, nombres.get(1))
                    Auxiliar().colocarImagen(challenge5, urls.get(2), editText3, nombres.get(2))

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
