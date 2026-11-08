
package com.example.shuttwareapp.screens

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.model.Sesion
import com.example.shuttwareapp.components.BotonSalir
import com.example.shuttwareapp.components.TarjetaSesion
import com.example.shuttwareapp.components.formatearFecha
import com.example.shuttwareapp.viewmodel.SesionesViewModel
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MisSesionesScreen(
    apiService: ApiService,
    idUsuario: Int,
    nombre: String,
    sesionesViewModel: SesionesViewModel,
    onSesionClick: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    // Función local para no repetir la tarjeta
    val tarjeta: @Composable (Sesion) -> Unit = { sesion ->
        TarjetaSesion(
            sesion = sesion,
            onClick = onSesionClick,
            onCambiarEntrega = { id, tipo ->
                sesionesViewModel.cambiarTipoEntrega(apiService, id, tipo, idUsuario)
            }
        )
    }

    // Cargar al entrar en la pantalla
    LaunchedEffect(idUsuario) {
        sesionesViewModel.cargarSesiones(apiService, idUsuario)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF344B66),
                        Color(0xFF617F9B),
                        Color(0xFFC5A18F),
                        Color(0xFFFFD0B0)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(
                        Float.POSITIVE_INFINITY,
                        Float.POSITIVE_INFINITY
                    )
                )
            )
    ) {

        when {
            sesionesViewModel.cargando -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            sesionesViewModel.error != null -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sesionesViewModel.error!!,
                        color = Color.White
                    )
                }
            }

            sesionesViewModel.sesiones.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Todavía no tienes sesiones",
                        color = Color.White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(
                            text = "Hola $nombre",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 15.sp
                        )

                        Text(
                            text = "M I S   S E S I O N E S",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 3.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "FILTRAR SESIONES",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )

                            SelectorDesplegable(
                                titulo = "Estado",
                                opciones = sesionesViewModel.estados,
                                seleccion = sesionesViewModel.estadoSeleccionado,
                                onSeleccionar = {
                                    sesionesViewModel.cambiarEstado(it)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SelectorFecha(
                                    titulo = "Desde",
                                    fecha = sesionesViewModel.fechaInicio,
                                    modifier = Modifier.weight(1f),
                                    onSeleccionar = {
                                        mostrarSelectorFecha(
                                            context,
                                            sesionesViewModel.fechaInicio
                                        ) {
                                            sesionesViewModel.cambiarFechaInicio(it)
                                        }
                                    },
                                    onLimpiar = {
                                        sesionesViewModel.cambiarFechaInicio(null)
                                    }
                                )

                                SelectorFecha(
                                    titulo = "Hasta",
                                    fecha = sesionesViewModel.fechaFin,
                                    modifier = Modifier.weight(1f),
                                    onSeleccionar = {
                                        mostrarSelectorFecha(
                                            context,
                                            sesionesViewModel.fechaFin
                                        ) {
                                            sesionesViewModel.cambiarFechaFin(it)
                                        }
                                    },
                                    onLimpiar = {
                                        sesionesViewModel.cambiarFechaFin(null)
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${sesionesViewModel.sesionesFiltradas.size} sesiones",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Text(
                                    text = "Limpiar filtros",
                                    modifier = Modifier
                                        .clickable {
                                            sesionesViewModel.limpiarFiltros()
                                        }
                                        .padding(4.dp),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (sesionesViewModel.sesionesFiltradas.isEmpty()) {
                        item {
                            Text(
                                text = "No hay sesiones con estos filtros",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (sesionesViewModel.sesionesPendientes.isNotEmpty()) {
                        item {
                            TituloSeccion(
                                "PENDIENTES",
                                Color(0xFFFFC107),
                                sesionesViewModel.sesionesPendientes.size
                            )
                        }

                        items(sesionesViewModel.sesionesPendientes) { sesion ->
                            tarjeta(sesion)
                        }
                    }

                    if (sesionesViewModel.sesionesEnCurso.isNotEmpty()) {
                        item {
                            TituloSeccion(
                                "EN PROCESO",
                                Color(0xFF2196F3),
                                sesionesViewModel.sesionesEnCurso.size
                            )
                        }

                        items(sesionesViewModel.sesionesEnCurso) { sesion ->
                            tarjeta(sesion)
                        }
                    }

                    if (sesionesViewModel.sesionesEntregadas.isNotEmpty()) {
                        item {
                            TituloSeccion(
                                "ENTREGADAS",
                                Color(0xFF4CAF50),
                                sesionesViewModel.sesionesEntregadas.size
                            )
                        }

                        items(sesionesViewModel.sesionesEntregadas) { sesion ->
                            tarjeta(sesion)
                        }
                    }

                    if (sesionesViewModel.otrasSesiones.isNotEmpty()) {
                        item {
                            TituloSeccion(
                                "OTRAS SESIONES",
                                Color(0xFF9E9E9E),
                                sesionesViewModel.otrasSesiones.size
                            )
                        }

                        items(sesionesViewModel.otrasSesiones) { sesion ->
                            tarjeta(sesion)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
        ) {
            BotonSalir(onLogout = onLogout)
        }

    }
}

@Composable
private fun SelectorDesplegable(
    titulo: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = titulo,
            color = Color.White,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = true },
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Text(
                    text = seleccion.replace("_", " ")
                        .replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 12.dp
                    ),
                    color = Color(0xFF191E25),
                    fontSize = 13.sp
                )
            }

            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                opcion.replace("_", " ")
                                    .replaceFirstChar { it.uppercase() }
                            )
                        },
                        onClick = {
                            onSeleccionar(opcion)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorFecha(
    titulo: String,
    fecha: String?,
    modifier: Modifier = Modifier,
    onSeleccionar: () -> Unit,
    onLimpiar: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = titulo,
            color = Color.White,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSeleccionar() },
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Text(
                    text = fecha?.let { formatearFecha(it) }
                        ?: "Seleccionar",
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    ),
                    color = Color(0xFF191E25),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            if (fecha != null) {
                Text(
                    text = "×",
                    modifier = Modifier
                        .clickable { onLimpiar() }
                        .padding(start = 6.dp),
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }
    }
}

private fun mostrarSelectorFecha(
    context: android.content.Context,
    fechaActual: String?,
    onFechaSeleccionada: (String) -> Unit
) {
    val calendario = Calendar.getInstance()

    if (fechaActual != null) {
        val partes = fechaActual.split("-")

        if (partes.size == 3) {
            calendario.set(
                partes[0].toInt(),
                partes[1].toInt() - 1,
                partes[2].toInt()
            )
        }
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            onFechaSeleccionada(
                "%04d-%02d-%02d".format(
                    year,
                    month + 1,
                    day
                )
            )
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
private fun TituloSeccion(
    texto: String,
    color: Color,
    cantidad: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = texto,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            letterSpacing = 1.5.sp,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = cantidad.toString(),
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}