package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clases.Auxiliar
import com.example.clases.Confirmacion
import kotlinx.android.synthetic.main.activity_info_challenge.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class info_challenge : AppCompatActivity() {

    lateinit var confirmacion: Confirmacion
    lateinit var challengeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_challenge)

        val arregloRecibido = intent?.getStringArrayExtra("arreglo")
        challengeId = arregloRecibido!![0]

        println(challengeId)

        cargarConfirmacion()
    }

    fun estaActividad(): info_challenge {
        return this
    }

    fun cargarConfirmacion(){
        GlobalScope.launch {
            // code, megustas, info
            verificarSubscripcionChallege()
        }
    }
    fun iniciarEventos(){
        if (confirmacion.meGustas == 0) {
            unirseGrupo.setBackgroundResource(R.drawable.unirse)
        } else {
            unirseGrupo.setBackgroundResource(R.drawable.chat)
        }
        unirseGrupo.setOnClickListener {
            /*
            val intent = Intent(this, contr_grupoxchalenge::class.java)
            intent.putExtra("arreglo", arrayOf(idChallenge))
            startActivity(intent)
            */
        }
        meGustaInfo.setOnClickListener {
            GlobalScope.launch {
                meEncantaCt(challengeId.toInt())
            }
        }
    }

    private fun inicializarComponentesGUI() {
        if( confirmacion.meGustas == 0 || confirmacion.meGustas == 2){
            meGustaInfo.setBackgroundResource(R.drawable.me_encanta)
        } else if ( confirmacion.meGustas == 1){
            meGustaInfo.setBackgroundResource(R.drawable.no_me_encanta)
        }
        if ( confirmacion.meGustas == 2 ){
            unirseGrupo.setBackgroundResource(R.drawable.unirse)
        } else {
            unirseGrupo.setBackgroundResource(R.drawable.chat)
        }
    }
    // DAR ME GUSTA
    private suspend fun meEncantaCt(challengeCodigo: Int) {
        val resultadoSolicitud = meEncanta(challengeCodigo)
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(resultadoSolicitud)) {
                1, 2 -> {
                    meGustaInfo.setBackgroundResource(R.drawable.no_me_encanta)
                }
                0 -> {
                    meGustaInfo.setBackgroundResource(R.drawable.me_encanta)
                }
                -1 -> {
                    println("Caido")
                }
            }
        }
    }

    private suspend fun meEncanta(challengeCodigo: Int): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "meEncantaChallenge",
                    "{\"idChallenge\":$challengeCodigo," +
                            "\"idEstudiante\":${ getString(R.string.idEstudiante).toInt()} }")
            println(Auxiliar().respuestaString(solicitud.body()))
            return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
        }
    }

    private suspend fun verificarSubscripcionChallege(){
        println("verificar")
        val solicitud = subscripcionChallege()
        withContext(Dispatchers.Main) {
            val resp = Auxiliar().mensajeServidor(solicitud)
            when (resp) {
                0 -> print("Servidor caido")
                else -> {
                    if(solicitud.length() > 0){
                        println(solicitud.getJSONObject(0).toString())
                        confirmacion =  Auxiliar().objectoConfirmacion(solicitud.getJSONObject(0))
                        inicializarComponentesGUI()
                        iniciarEventos()
                    }
                }
            }
        }
    }

    private suspend fun subscripcionChallege(): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "returnConfirmation",
                    "{\"idChallenge\":${challengeId.toInt()}, \"idEstudiante\": ${
                    getString(R.string.idEstudiante).toInt()
                    }}")
            // println(JSONArray(solicitud.body().string().toString()))
            return@withContext JSONArray(solicitud.body().string().toString())
        }
    }


}
