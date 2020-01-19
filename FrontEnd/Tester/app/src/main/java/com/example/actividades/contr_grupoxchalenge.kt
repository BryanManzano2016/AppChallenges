package com.example.actividades
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView


class contr_grupoxchalenge : AppCompatActivity() {
    private lateinit var  lViewgrupos: ListView
    private val nombresGrupos = arrayOf( "Grupo Carissa", "Grupo Dragonfly", "Grupo kiwi", "Grupo Okra", "Grupo Zafiro", "Grupo Reales", "Grupo Dureza", "Grupo Fuego", "Grupo Granada")
    //var imgdefaut = arrayOf( R.drawable.icondefault,R.drawable.icondefault,R.drawable.icondefault,R.drawable.icondefault,R.drawable.icondefault,R.drawable.icondefault,R.drawable.icondefault,R.drawable.icondefault,R.drawable.icondefault)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupoxchallenge)
        lViewgrupos= findViewById(R.id.lViewgrupos)
        val adaptador1=ArrayAdapter<String>(this, R.layout.list_item_grupos, nombresGrupos)
        lViewgrupos.adapter = adaptador1
        lViewgrupos.setOnItemClickListener { adapterView, view, i, l ->
            setContentView(R.layout.activity_pall_infogrupo)}
    }

    fun obtenerGrupos(){

    }
}
