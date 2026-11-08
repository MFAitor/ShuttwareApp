package com.example.shuttwareapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shuttwareapp.api.ApiService
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    var perfil by mutableStateOf<Map<String, Any>?>(null)
        private set

    var cargandoPerfil by mutableStateOf(false)
        private set

    var mensaje by mutableStateOf<String?>(null)
        private set

    var error by mutableStateOf<String?>(null)

    fun cargarPerfil(apiService: ApiService, id: Int) {
        cargandoPerfil = true
        error = null
        viewModelScope.launch {
            try {
                val response = apiService.verCliente(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    perfil = body?.get("cliente") as? Map<String, Any>
                } else {
                    error = "No se pudo cargar el perfil"
                }
            } catch (e: Exception) {
                error = "Error de conexión"
            } finally {
                cargandoPerfil = false
            }
        }
    }

    fun guardarPerfil(
        apiService: ApiService,
        id: Int,
        nombre: String,
        apellidos: String,
        telefono: String,
        instagram: String,
        onSuccess: () -> Unit
    ) {
        error = null
        mensaje = null
        viewModelScope.launch {
            try {
                val datos = mapOf(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "telefono" to telefono,
                    "instagram" to instagram
                )
                val response = apiService.modificarCliente(id, datos)
                if (response.isSuccessful) {
                    val body = response.body()
                    val ok = body?.get("ok") as? Boolean ?: false
                    if (ok) {
                        mensaje = "Datos modificados"
                        cargarPerfil(apiService, id)
                        onSuccess()
                    } else {
                        error = body?.get("mensaje") as? String ?: "Error al guardar"
                    }
                } else {
                    error = "Error del servidor"
                }
            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }

    fun cambiarContrasena(
        apiService: ApiService,
        id: Int,
        actual: String,
        nueva: String,
        onSuccess: () -> Unit
    ) {
        error = null
        viewModelScope.launch {
            try {
                val response = apiService.cambiarContrasena(
                    id,
                    mapOf(
                        "actual" to actual,
                        "nueva" to nueva
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val ok = body?.get("ok") as? Boolean ?: false
                    if (ok) {
                        onSuccess()
                    } else {
                        error = body?.get("mensaje") as? String ?: "No se pudo cambiar la contraseña"
                    }
                } else {
                    error = "Error del servidor"
                }
            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }

    fun limpiarMensaje() {
        mensaje = null
    }
}