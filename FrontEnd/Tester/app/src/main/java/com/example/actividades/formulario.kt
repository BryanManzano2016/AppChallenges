package com.example.actividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clases.Auxiliar
import kotlinx.android.synthetic.main.activity_formulario.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class formulario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)
        crear()

    }

    fun crear(){
        botonagregar.setOnClickListener {
            GlobalScope.launch {

                verificarCrearGrupo("Dinamita","Mejores malo","sdsdsdsfdsf")
            }
        }
    }




    private suspend fun llenarFormulario(nombreGrupo: String, Descripcion: String, url_wp: String): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "crearGrupo",
                    "{\"nombreGrupo\":${nombreGrupo}, \"Descripcion\": ${Descripcion}, \"url_wp\": ${url_wp}}")
            return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
        }
    }

    private suspend fun verificarCrearGrupo(nombreGrupo: String, Descripcion: String, url_wp: String){
        val solicitud = llenarFormulario(nombreGrupo,Descripcion,url_wp)
        withContext(Dispatchers.Main) {

            val arregloEnviar = arrayOf("2")
            when (Auxiliar().mensajeServidor(solicitud)) {
                0 -> print("Servidor caido")
                1 -> {
                    val intent = Intent(this@formulario, contr_grupoxchalenge::class.java)
                    intent.putExtra("arreglo", arregloEnviar)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(this@formulario, contr_grupoxchalenge::class.java)
                    intent.putExtra("arreglo", arregloEnviar)
                    startActivity(intent)
                }
            }
        }
    }

}
