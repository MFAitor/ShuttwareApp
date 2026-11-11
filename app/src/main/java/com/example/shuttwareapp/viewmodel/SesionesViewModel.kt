package com.example.shuttwareapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.model.Sesion
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class SesionesViewModel : ViewModel() {

    //Se guarda el estado de las sesiones con variables mutables para saber cuando se cambian sus estados
    var sesiones by mutableStateOf<List<Sesion>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var estadoSeleccionado by mutableStateOf("Todos")
        private set

    var fechaInicio by mutableStateOf<String?>(null)
        private set

    var fechaFin by mutableStateOf<String?>(null)
        private set

    //Estados que se pueden seleccionar en los filtros
    val estados = listOf(
        "Todos",
        "pendiente",
        "En proceso",
        "entregada",
        "rechazada",
        "cancelada",
        "confirmada",
        "realizada",
        "en_edicion",
        "lista_entrega"
    )

    //Filtramos las sesiones por estado y fecha
    val sesionesFiltradas: List<Sesion>
        get() = sesiones.filter { sesion ->

            val coincideEstado = when (estadoSeleccionado) {
                "Todos" -> true

                "En proceso" -> sesion.estado in listOf(
                    "confirmada",
                    "realizada",
                    "en_edicion",
                    "lista_entrega"
                )

                else -> sesion.estado == estadoSeleccionado
            }

            val fechaSesion = sesion.fecha.take(10)

            val coincideFechaInicio =
                fechaInicio == null || fechaSesion >= fechaInicio!!

            val coincideFechaFin =
                fechaFin == null || fechaSesion <= fechaFin!!

            coincideEstado && coincideFechaInicio && coincideFechaFin
        }

    //Separamos las sesiones segun su estado
    val sesionesPendientes: List<Sesion>
        get() = sesionesFiltradas.filter { it.estado == "pendiente" }

    val sesionesEnCurso: List<Sesion>
        get() = sesionesFiltradas.filter {
            it.estado in listOf("confirmada", "realizada", "en_edicion", "lista_entrega")
        }

    val sesionesEntregadas: List<Sesion>
        get() = sesionesFiltradas.filter { it.estado == "entregada" }

    val otrasSesiones: List<Sesion>
        get() = sesionesFiltradas.filter {
            it.estado in listOf("rechazada", "cancelada")
        }

    //Carga de las sesiones existentes
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarSesiones(apiService: ApiService, idUsuario: Int) {

        cargando = true
        error = null

        viewModelScope.launch {
            try {

                val todas = apiService.listarSesiones(idUsuario)
                val conPreviews = apiService.sesionesConPreviews(idUsuario)

                val idsConPreviews = conPreviews.map { it.idSesion }.toSet()

                //Hay que mappear bien las fechas para que aparezcan en el formato correcto
                sesiones = todas.map { s ->

                    println("DEBUG → id=${s.idSesion} estado=${s.estado} total=${s.totalFotos} editadas=${s.fotosEditadas}")

                    //Conversion de fecha de la recibida por API por la que queremos por pantalla
                    val fechaCorregida = try {
                        Instant.parse(s.fecha)
                            .atZone(ZoneId.of("Europe/Madrid"))
                            .toLocalDate()
                            .toString()
                    } catch (e: Exception) {
                        s.fecha.take(10)
                    }

                    val preview = conPreviews.find { it.idSesion == s.idSesion }

                    s.copy(
                        fecha = fechaCorregida,
                        tienePreviews = idsConPreviews.contains(s.idSesion),
                        numPreviews = preview?.numPreviews ?: 0
                    )
                }

            } catch (e: Exception) {
                error = "No se pudieron cargar las sesiones"
            } finally {
                cargando = false
            }
        }
    }

    //Cambiamos el estado seleccionado
    fun cambiarEstado(estado: String) {
        estadoSeleccionado = estado
    }

    //Cambiamos las fechas de los filtros
    fun cambiarFechaInicio(fecha: String?) {
        fechaInicio = fecha
    }

    fun cambiarFechaFin(fecha: String?) {
        fechaFin = fecha
    }

    //Volvemos a dejar los filtros como estaban al principio
    fun limpiarFiltros() {
        estadoSeleccionado = "Todos"
        fechaInicio = null
        fechaFin = null
    }

    //Funcion que ayuda a usar el porcentaje de fotos editadas en la UI
    fun porcentajeEdicion(sesion: Sesion): Int? {

        val total = sesion.totalFotos ?: return null
        val editadas = sesion.fotosEditadas ?: return null

        if (total <= 0 || sesion.estado != "en_edicion") {
            return null
        }

        return (editadas * 100) / total
    }

    //Metodo que cambia el tipo de entrega de la sesion cuando esta ya esta creada
    fun cambiarTipoEntrega(
        apiService: ApiService,
        idSesion: Int,
        tipoEntrega: String,
        idUsuario: Int
    ) {

        viewModelScope.launch {
            try {

                //Datos que se envian al API
                val body = mapOf(
                    "tipoEntrega" to tipoEntrega,
                    "idUsuario" to idUsuario
                )

                println("DEBUG body: $body")

                //Se envian los datos para actualizar la sesion
                val response = apiService.actualizarSesion(idSesion, body)

                println("DEBUG codigo: ${response.code()}")
                println("DEBUG cuerpo: ${response.body()}")
                println("DEBUG errorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.get("ok") == true) {

                    //Actualizamos la sesion con el nuevo tipo de entrega
                    sesiones = sesiones.map { s ->
                        if (s.idSesion == idSesion) {
                            s.copy(tipoEntrega = tipoEntrega)
                        } else {
                            s
                        }
                    }

                    //Se guarda el mensaje de error
                } else {

                    val mensaje = response.body()?.get("mensaje")?.toString()
                        ?: response.errorBody()?.string()
                        ?: "No se pudo cambiar el tipo de entrega"

                    error = mensaje
                }

            } catch (e: Exception) {
                e.printStackTrace()
                error = "Error al cambiar entrega: ${e.message}"
            }
        }
    }
}