package com.example.clases

class Grupos( val grupoNombre: String, val descripcion: String, val codeGrupo: String,
              val url_whatsapp: String) {
    override fun toString(): String {
        return "Challenge(grupoNombre='$grupoNombre', descripcion='$descripcion', codeGrupo='$codeGrupo',  url_whatsapp='$url_whatsapp')"
    }
}


