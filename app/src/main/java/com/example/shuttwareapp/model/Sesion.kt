package com.example.shuttwareapp.model

import com.google.gson.annotations.SerializedName

data class Sesion(
    @SerializedName("id_sesion")
    val idSesion: Int = 0,

    @SerializedName("fecha")
    val fecha: String = "",

    @SerializedName("tipo")
    val tipo: String? = null,

    @SerializedName("estado")
    val estado: String = "",

    @SerializedName("tiene_previews")
    val tienePreviews: Boolean = false,

    @SerializedName("num_previews")
    val numPreviews: Int = 0,

    @SerializedName("total_fotos")
    val totalFotos: Int? = null,

    @SerializedName("fotos_editadas")
    val fotosEditadas: Int? = null,

    @SerializedName("tipo_entrega")
    val tipoEntrega: String? = null
)