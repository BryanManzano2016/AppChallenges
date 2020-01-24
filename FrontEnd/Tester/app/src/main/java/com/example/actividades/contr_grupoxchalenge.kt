package com.example.actividades
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.example.clases.Auxiliar
import com.example.clases.Challenge
import com.example.clases.Grupos
import kotlinx.android.synthetic.main.activity_grupoxchallenge.*
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

        val arregloRecibido = intent?.getStringArrayExtra("arreglo")

        iniciarComponentesUI(arregloRecibido?.get(0)?.toInt()!!)
    }

    fun iniciarComponentesUI(id: Int){
        GlobalScope.launch { obtenerGruposChallengesCt(id) }
        botonagregar2.setOnClickListener {
            val arregloEnviar = arrayOf("2")
            val intent = android.content.Intent(this, formulario::class.java)
            intent.putExtra("arreglo", arregloEnviar)
            startActivity(intent)
        }
    }

    private suspend fun obtenerGruposChallengesCt(cat : Int) {
        val resultadoSolicitud = obtenerGruposChallenges(cat)
        withContext(Dispatchers.Main) {
            when (Auxiliar().mensajeServidor(resultadoSolicitud)) {
                0 -> println("0")
                -1 -> {
                    lateinit var lViewgrupos: ListView
                    val nombresGrupos = LinkedList<Grupos>()
                    val nombres=LinkedList<String>()
                    for (i in 0 until resultadoSolicitud.length()) {
                        nombresGrupos.add(Auxiliar().objetoGrupo(resultadoSolicitud.getJSONObject(i)))
                        println(resultadoSolicitud.getJSONObject(i).toString())
                    }
                    nombresGrupos.forEach { nombres.add(it.grupoNombre) }
                    lViewgrupos= findViewById(R.id.lViewgrupos)
                    var adaptador1= ArrayAdapter<String>(this@contr_grupoxchalenge, R.menu.list_item_grupos, nombres)
                    lViewgrupos.adapter = adaptador1
                    lViewgrupos.setOnItemClickListener { adapterView, view, i, l ->
                        setContentView(R.layout.activity_pall_infogrupo)}
                    barrabusqueda.addTextChangedListener( object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {}
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence, start: Int,
                                                   before: Int, count: Int) {
                            adaptador1.filter.filter(s)
                        }
                    })
                }
            }
        }
    }

    private suspend fun obtenerGruposChallenges(id: Int): JSONArray {
        return withContext(Dispatchers.Default) {
            val solicitud = Auxiliar().solicitudHttpPost(
                    Auxiliar().obtener_Ip() + "obtenerGruposChallenges",
                    "{\"idChallenge\":$id}"
            )
            return@withContext JSONArray(Auxiliar().respuestaString(solicitud.body()))
        }

    }

}
