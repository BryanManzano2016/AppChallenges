package com.example.clases

class Confirmacion( val id: Int, val disponible: Int, val codigoChallenge: Int,
                 val meGustas: Int, val idEstudiante: Int) {
    override fun toString(): String {
        return "Confirmacion(id='$id', disponible='$disponible', codigoChallenge='$codigoChallenge', meGustas=$meGustas, idEstudiante='$idEstudiante')"
    }
}