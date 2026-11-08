package com.example.shuttwareapp.model

data class SolicitudSesion(
    val idUsuario: Int,
    val idHueco: Int,
    val tipo: String,
    val lugar: String,
    val nota: String,
    val tipoEntrega: String? = null
)