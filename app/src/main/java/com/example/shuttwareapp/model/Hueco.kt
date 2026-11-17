package com.example.shuttwareapp.model

import java.time.LocalDate

///Clase que representa un hueco en la base de datos
data class Hueco(
    val id: Int,
    val fecha: LocalDate,
    val estado: String
)