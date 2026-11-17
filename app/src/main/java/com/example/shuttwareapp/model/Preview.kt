package com.example.shuttwareapp.model

//Representa la preview de la imagen editada que el fotografo ha subido a la base de datos
data class Preview(
    val id: Int,
    val idSesion: Int,
    val ruta: String,
    val pie: String?,
    val orden: Int
)