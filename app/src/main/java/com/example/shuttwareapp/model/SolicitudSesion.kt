package com.example.shuttwareapp.model

//Clase que usa el cliente para un hueco con los datos mínimos para ello
data class SolicitudSesion(
    val idUsuario: Int,
    val idHueco: Int,
    val tipo: String,
    val lugar: String,
    val nota: String,
    val tipoEntrega: String? = null
)