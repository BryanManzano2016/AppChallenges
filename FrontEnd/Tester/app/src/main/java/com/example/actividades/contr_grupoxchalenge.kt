package com.example.actividades
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.clases.Auxiliar
import com.example.clases.Challenge
import com.example.clases.Grupos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*


class contr_grupoxchalenge : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupoxchallenge)

        GlobalScope.launch { obtenerGruposChallengesCt("2") }

    }


        private suspend fun obtenerGruposChallenges(id:String ): JSONArray {
            return withContext(Dispatchers.Default) {
                val solicitud = Auxiliar().solicitudHttpPost(
                        Auxiliar().obtener_Ip() + "obtenerGruposChallenges",
                        "{\"idChallenge\":\""+id+"\"\"}") // debo comprobar esto...
                return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
            }

    }

    private suspend fun obtenerGruposChallengesCt(cat : String) {
        val resultadoSolicitud = obtenerGruposChallenges(cat)
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(resultadoSolicitud)) {
                0 -> print("0")
                -1 -> {
                    lateinit var  lViewgrupos: ListView
                    val nombresGrupos = LinkedList<Grupos>()
                    for (i in 0 until resultadoSolicitud.length()) {
                        nombresGrupos.add(Auxiliar().objetoGrupo(resultadoSolicitud.getJSONObject(i)))
                    }

                    lViewgrupos= findViewById(R.id.lViewgrupos)
                    val adaptador1= ArrayAdapter<Grupos>(this@contr_grupoxchalenge, R.menu.list_item_grupos, nombresGrupos)
                    lViewgrupos.adapter = adaptador1
                    lViewgrupos.setOnItemClickListener { adapterView, view, i, l ->
                        setContentView(R.layout.activity_pall_infogrupo)}

                }
            }
        }
    }
}
