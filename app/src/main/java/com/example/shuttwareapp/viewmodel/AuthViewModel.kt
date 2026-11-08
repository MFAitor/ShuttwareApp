package com.example.shuttwareapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shuttwareapp.api.ApiService
import kotlinx.coroutines.launch
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shuttwareapp.worker.HuecosWorker
import com.example.shuttwareapp.worker.PreviewsWorker
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    //Almacenar los datos del API, tanto nombre como id de usuario
    var idUsuario by mutableStateOf<Int?>(null)
        private set
    var nombreUsuario by mutableStateOf<String?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun login(
        context: Context,
        apiService: ApiService,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        error = null

        // Validación de campos vacíos
        if (email.isBlank() || password.isBlank()) {
            error = "Debes introducir el email y la contraseña"
            return
        }

        viewModelScope.launch {
            try {
                val response = apiService.login(
                    mapOf(
                        "email" to email,
                        "contrasenia" to password
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val usuario = body?.get("usuario") as? Map<*, *>

                    val id = (usuario?.get("id_usuario") as? Number)?.toInt()
                    val nombre = usuario?.get("nombre") as? String

                    if (id != null) {
                        idUsuario = id
                        nombreUsuario = nombre

                        //Guardar el id en preferencias para que lo pueda usar el Worker
                        val prefs = context.getSharedPreferences("shuttware", Context.MODE_PRIVATE)
                        prefs.edit().putInt("id_usuario", id).apply()

                        // Programar la comprobacion periodica de huecos nuevos
                        programarComprobacionHuecos(context)
                        programarComprobacionPreviews(context)

                        onSuccess()
                    } else {
                        // La API respondio OK pero sin usuario valido
                        error = "Datos de usuario incorrectos"
                    }
                } else {
                    // La API respondiobb con error HTTP
                    error = "Email o contraseña incorrectos"
                }
            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }

    private fun programarComprobacionHuecos(context: Context) {
        val request = PeriodicWorkRequestBuilder<HuecosWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "comprobar_huecos",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    //Metodo para la creacion de un nuevo usuario, y entrar en su cuenta acto seguido
    fun registro(
        context: Context,
        apiService: ApiService,
        nombre: String,
        apellidos: String,
        email: String,
        password: String,
        telefono: String?,
        instagram: String?,
        onSuccess: () -> Unit
    ) {
        error = null
        viewModelScope.launch {
            try {
                val datos = mutableMapOf(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "email" to email,
                    "contrasenia" to password
                )
                if (!telefono.isNullOrBlank()) datos["telefono"] = telefono
                if (!instagram.isNullOrBlank()) datos["url_instagram"] = instagram

                val response = apiService.registro(datos)

                if (response.isSuccessful) {
                    val body = response.body()
                    val ok = body?.get("ok") as? Boolean ?: false
                    if (ok) {
                        //Login automático
                        login(context,apiService, email, password, onSuccess)
                    } else {
                        error = body?.get("mensaje") as? String ?: "No se pudo registrar"
                    }
                } else {
                    error = "Error del servidor"
                }
            } catch (e: Exception) {
                error = "Error de conexión"
            }
        }
    }

    //Comprobar si se han subido nuevas previews
    private fun programarComprobacionPreviews(context: Context) {
        val request = PeriodicWorkRequestBuilder<PreviewsWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "comprobar_previews",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    //Metodo para desloguearse y reiniciar los datos
    fun logout() {
        idUsuario = null
        nombreUsuario = null
        error = null
    }

    fun limpiarError() {
        error = null
    }
}