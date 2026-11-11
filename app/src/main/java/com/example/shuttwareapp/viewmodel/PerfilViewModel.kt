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

    // Cargamos los datos del perfil del usuario
    fun cargarPerfil(apiService: ApiService, id: Int) {
        cargandoPerfil = true
        error = null

        viewModelScope.launch {
            try {
                //Se consultan los datos del cliente por el API
                val response = apiService.verCliente(id)

                //En caso de cargar se guardan estos datos
                if (response.isSuccessful) {
                    val body = response.body()

                    perfil = body?.get("cliente") as? Map<String, Any>
                } else {
                    error = "No se pudo cargar el perfil"
                }

            } catch (e: Exception) {
                error = "Error de conexión"

            } finally {
                //Indicamos que la carga ha terminado
                cargandoPerfil = false
            }
        }
    }

    //Guardamos los cambios realizados en el perfil
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
                //Preparamos los datos que enviaremos a la API
                val datos = mutableMapOf<String, Any>(
                    "nombre" to nombre.trim()
                )

                //En caso de que alguno de los siguientes datos se mande vacio no se tendra en cuenta
                if (apellidos.isNotBlank()) datos["apellidos"] = apellidos.trim()

                if (telefono.isNotBlank()) {
                    val tel = telefono.trim().toIntOrNull()
                    if (tel != null) {
                        datos["telefono"] = tel
                    }
                }

                if (instagram.isNotBlank()) datos["url_instagram"] = instagram.trim()

                val response = apiService.modificarCliente(id, datos)

                //Si la peticion al API se ha realizado  de cargan y se vuelve a cargar la pantalla con los nuevos datos
                if (response.isSuccessful) {
                    val body = response.body()
                    val ok = body?.get("ok") as? Boolean ?: false

                    if (ok) {
                        mensaje = "Datos modificados"

                        // Volvemos a cargar el perfil con los datos actualizados
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

    //El cambio de contraseña es distinto porque hay que ingresar la anterior
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
                //Enviamos la contraseña actual y la nueva
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
                        error = body?.get("mensaje") as? String
                            ?: "No se pudo cambiar la contraseña"
                    }
                } else {
                    error = "Error del servidor"
                }

            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }

    //Limpiamos el mensaje mostrado
    fun limpiarMensaje() {
        mensaje = null
    }
}