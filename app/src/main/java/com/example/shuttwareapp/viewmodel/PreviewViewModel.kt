package com.example.shuttwareapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shuttwareapp.model.Preview
import com.example.shuttwareapp.api.ApiService
import kotlinx.coroutines.launch

class PreviewsViewModel : ViewModel() {
    var previews by mutableStateOf<List<Preview>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun cargar(api: ApiService, idSesion: Int, idUsuario: Int) {
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                val response = api.listarPreviews(idSesion, idUsuario)
                if (response.isSuccessful) {
                    val body = response.body()
                    val ok = body?.get("ok") == true
                    if (ok) {
                        val lista = body?.get("previews") as? List<Map<String, Any>> ?: emptyList()
                        previews = lista.map { m ->
                            Preview(
                                id = (m["id_preview"] as Number).toInt(),
                                idSesion = idSesion,
                                //La ruta utiliza la direccion de la ip para poder cargar las imagenes

                                //Prueba para acceder desde SpringBoot
                                ruta = m["ruta"] as? String ?: "",

                                //Prueba para ngrok
                                //ruta = "https://retreat-disrupt-recoup.ngrok-free.dev/previews/imagen/${(m["id_preview"] as Number).toInt()}",

                                pie = m["pie"] as? String,
                                orden = (m["orden"] as Number).toInt()
                            )
                        }
                    } else {
                        error = body?.get("mensaje")?.toString() ?: "Error"
                    }
                } else {
                    error = "Error del servidor"
                }
            } catch (e: Exception) {
                error = "No se pudieron cargar las previews"
            } finally {
                cargando = false
            }
        }
    }
}