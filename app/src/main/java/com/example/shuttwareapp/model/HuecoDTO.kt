package com.example.shuttwareapp.model

//Uso una clase HuecoDTO que servira para recibir los datos del API
data class HuecoDto(
    val id_hueco: Int,
    val fecha: String,
    val estado: String
)