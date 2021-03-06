package com.example.clases

import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.okhttp.*
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.security.acl.Group
import kotlin.contracts.Returns

class Auxiliar {


    // val direccion_ip= "http://192.168.100.81:9000/"
    // val direccion_ip= "http://192.168.200.11:9000/"
    val direccion_ip= "http://192.168.43.246:9000/"


 //   val direccion_ip= "http://192.168.100.133:9000/"

    fun obtener_Ip(): String {
        return direccion_ip.toString()
    }

    // Genera una solicitud http en formato get dado una url
    fun solicitudHttpGet(url: String): Response{
        println("***SOLICITUD GET***")
        // Ejemplo de url -> "http://192.168.100.133:9000/obtenerChallenges"
        return OkHttpClient().newCall( Request.Builder()
                .url(url)
                .build() ).execute()
    }

    fun solicitudHttpPost(url: String, cadenaEnvio: String): Response{
        println("***SOLICITUD POST***")
        val body: RequestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                cadenaEnvio)
        return OkHttpClient().newCall( Request.Builder()
                .url(url)
                .post(body)
                .build()).execute()
    }

    // Obtiene string desde el cuerpo de una respuesta Http
    fun respuestaString(respuesta: ResponseBody?): String{
        return respuesta?.string().toString()
    }

    /*
        Toda respuesta del servidor es JSONArray, asi se verifica cuando el
        servidor envia solo un JSONObject con un campo respuesta
     */
    fun mensajeServidor(array: JSONArray): Int{
        return try {
            array.getJSONObject(0).getInt("respuesta")
        } catch (e: Exception){
            -1
        }
    }

    // Crear challenge a a partir d eun JSONObject
    fun objectoChallenge( objeto: JSONObject ): Challenge{
        return Challenge(
                objeto.get("nombre").toString(), objeto.get("url").toString(), objeto.get("info").toString(),
                objeto.get("categoria").toString(), objeto.get("meGustas").toString(),
                objeto.get("fechaInicio").toString(),objeto.get("code_challenges").toString()
        )
    }
    // Crear challenge a a partir d eun JSONObject
    fun objectoConfirmacion( objeto: JSONObject ): Confirmacion{
        return Confirmacion(
                objeto.getInt("id"), objeto.getInt("disponible"),
                objeto.getInt("idChallenges"), objeto.getInt("MeGusta"),
                objeto.getInt("estudiante")
        )
    }



    fun objetoGrupo(objeto: JSONObject): Grupos{
            return Grupos(
                    objeto.get("grupoNombre").toString(),objeto.get("descripcion").toString(),
                    objeto.get("codeGrupo").toString(), objeto.get("url_whatsapp").toString()
            )
    }

    fun colocarImagen(imagen: ImageView, url: String, texto: TextView, nombre: String){
        Picasso.get()
                .load(url)
                .resize(150, 150)
                .centerCrop()
                .into(imagen)
        texto.text=nombre
    }
    /*
    fun retornarFilaTabla(imagen: ImageButton, app: AppCompatActivity): TableRow{
        val elemento = TableRow(app)
        elemento.layoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
        elemento.addView(imagen)
        return elemento
    }
    */

}