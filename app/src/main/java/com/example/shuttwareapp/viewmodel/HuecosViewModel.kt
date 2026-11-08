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
import com.example.shuttwareapp.model.HuecoDto
import com.example.shuttwareapp.model.SolicitudSesion

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

    init {
        cargarHuecos()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarHuecos() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val dtos = api.obtenerHuecosLibres()

                //Datos para pruebas
                println("DTOs recibidos: ${dtos.size}")

                //Spring serializa la hora haciendo que haya descuadre de dia que hay que corregir
                _huecos.value = dtos.map { dto ->
                    val fecha = java.time.Instant.parse(dto.fecha)
                        .atZone(java.time.ZoneId.of("Europe/Madrid"))
                        .toLocalDate()

                    Hueco(
                        id = dto.id_hueco,
                        fecha = fecha,
                        estado = dto.estado
                    )

                }
                //Prueba
                println("Huecos mapeados: ${_huecos.value.size}")
            } catch (e: Exception) {
                //Prueba 2 lineas
                println("ERROR al cargar huecos: ${e.message}")
                e.printStackTrace()
                _mensaje.value = "No se pudieron cargar los huecos"
            } finally {
                _cargando.value = false
            }
        }
    }


    //Metodo que pasa los datos del usuario que quiere solicitar una sesion en un hueco libre
    fun solicitarHueco(hueco: Hueco, tipo: String, lugar: String, nota: String, idUsuario: Int, tipoEntrega: String? = null) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val datos = SolicitudSesion(
                    idUsuario = idUsuario,
                    idHueco = hueco.id,
                    tipo = tipo.trim(),
                    lugar = lugar,
                    nota = nota,
                    tipoEntrega = tipoEntrega
                )
                println("Enviando: $datos")

                val response = api.solicitarSesion(datos)
                println("Código HTTP: ${response.code()}")
                println("Body: ${response.body()}")

                val body = response.body()
                val ok = body?.get("ok") == true
                _mensaje.value = if (ok) "Sesión solicitada" else (body?.get("mensaje")?.toString() ?: "Error")
                if (ok) cargarHuecos()
            } catch (e: Exception) {
                println("Excepción: ${e.message}")
                e.printStackTrace()
                _mensaje.value = "Error al solicitar"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}