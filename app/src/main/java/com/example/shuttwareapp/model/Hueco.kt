package com.example.shuttwareapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDate.parse

///Clase que representa un hueco en la base de datos
data class Hueco(
    @SerializedName("id_hueco")
    val id: Int,
    val fecha: LocalDate,
    val estado: String
)