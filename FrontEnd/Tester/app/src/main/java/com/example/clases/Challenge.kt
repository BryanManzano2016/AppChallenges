package com.example.clases

class Challenge( val nombre: String, val url: String, val info: String,
                 val categoria: String, val meGustas: String, val fechaInicio: String) {
    override fun toString(): String {
        return "Challenge(nombre='$nombre', url='$url', info='$info', categoria='$categoria', meGustas='$meGustas', fechaInicio='$fechaInicio')"
    }
}


