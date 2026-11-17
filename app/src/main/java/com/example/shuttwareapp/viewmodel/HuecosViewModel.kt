package com.example.shuttwareapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.api.Red
import com.example.shuttwareapp.model.Hueco
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.map
import com.example.shuttwareapp.model.SolicitudSesion
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class HuecosViewModel(
    private val api: ApiService = Red.api
) : ViewModel() {

    private val _huecos = MutableStateFlow<List<Hueco>>(emptyList())
    val huecos: StateFlow<List<Hueco>> = _huecos.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Cargamos los huecos disponibles al crear el ViewModel
    init {
        cargarHuecos()
    }

    //Consultamos los huecos libres de la API
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarHuecos() {
        viewModelScope.launch {
            _cargando.value = true

            try {
                //La API devuelve Hueco
                val respuesta = api.obtenerHuecosLibres()
                _huecos.value = respuesta

            } catch (e: Exception) {
                println("ERROR al cargar huecos: ${e.message}")
                e.printStackTrace()
                _mensaje.value = "No se pudieron cargar los huecos"
            } finally {
                _cargando.value = false
            }
        }
    }

    //Metodo que envia los datos del usuario para solicitar una sesion
    fun solicitarHueco(
        hueco: Hueco,
        tipo: String,
        lugar: String,
        nota: String,
        idUsuario: Int,
        tipoEntrega: String? = null
    ) {
        viewModelScope.launch {
            _cargando.value = true

            try {
                //Preparamos los datos de la solicitud
                val datos = SolicitudSesion(
                    idUsuario = idUsuario,
                    idHueco = hueco.id,
                    tipo = tipo.trim(),
                    lugar = lugar,
                    nota = nota,
                    tipoEntrega = tipoEntrega
                )

                println("Enviando: $datos")

                //Enviamos la solicitud a la API
                val response = api.solicitarSesion(datos)

                //Comprobamos la respuesta recibida
                println("Codigo HTTP: ${response.code()}")
                println("Body: ${response.body()}")

                val body = response.body()
                val ok = body?.get("ok") == true

                //Mostramos el resultado de la solicitud
                _mensaje.value = if (ok) {
                    "Sesion solicitada"
                } else {
                    body?.get("mensaje")?.toString() ?: "Error"
                }

                //Si la solicitud ha ido bien, volvemos a cargar los huecos
                if (ok) {
                    cargarHuecos()
                }

            } catch (e: Exception) {
                //Mostramos el error para comprobar que ha ocurrido
                println("Excepcion: ${e.message}")
                e.printStackTrace()

                _mensaje.value = "Error al solicitar"

            } finally {
                //Mostrar que se termina la operacion
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}