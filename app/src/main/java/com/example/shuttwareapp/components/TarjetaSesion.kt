package com.example.shuttwareapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.R
import com.example.shuttwareapp.model.Sesion

//Funcion de formateo de estetica de las tarjetas de las sesiones y funcionamiento pulsable para ir a sus previews
@Composable
fun TarjetaSesion(
    sesion: Sesion,
    onClick: (Int) -> Unit,
    onCambiarEntrega: (Int, String) -> Unit = { _, _ -> }
) {
    var mostrarOpciones by remember { mutableStateOf(false) }

    //Se cargan los nombres de los tipos de entrega de manera mas entendible al usuario
    val etiquetasEntrega = mapOf(
        "link_descarga" to stringResource(R.string.link_de_descarga),
        "USB" to stringResource(R.string.usb),
        "album" to stringResource(R.string.lbum),
        "caja_personalizada" to stringResource(R.string.caja_personalizada)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(sesion.idSesion) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatearFecha(sesion.fecha),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = sesion.tipo ?: stringResource(R.string.sin_tipo),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = sesion.estado.replace("_", " ").replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp,
                        color = colorEstado(sesion.estado),
                        fontWeight = FontWeight.Medium
                    )

                    if (sesion.estado == "en_edicion") {
                        val total = sesion.totalFotos ?: 0
                        val editadas = sesion.fotosEditadas ?: 0
                        val porcentaje = if (total > 0) (editadas * 100) / total else 0

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.editado, porcentaje),
                            fontSize = 12.sp,
                            color = Color(0xFF5C6BC0),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { porcentaje / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = Color(0xFF5C6BC0),
                            trackColor = Color(0xFFE0E0E0)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (sesion.tienePreviews && sesion.numPreviews > 0) {
                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.preview,
                                    sesion.numPreviews,
                                    if (sesion.numPreviews != 1) "s" else ""
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 12.sp,
                                color = Color(0xFF1565C0),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.sin_previews),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    //Tipo de entrega que se puede cambiar pulsando
                    Text(
                        text = etiquetasEntrega[sesion.tipoEntrega] ?: stringResource(R.string.elegir_entrega),
                        fontSize = 12.sp,
                        color = Color(0xFF5B8DEF),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { mostrarOpciones = true }
                    )
                }
            }

            //Opciones de entrega desplegables
            if (mostrarOpciones) {
                Spacer(Modifier.height(8.dp))
                etiquetasEntrega.forEach { (valor, etiqueta) ->
                    Text(
                        text = etiqueta,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCambiarEntrega(sesion.idSesion, valor)
                                mostrarOpciones = false
                            }
                            .padding(vertical = 6.dp),
                        fontSize = 14.sp,
                        color = if (sesion.tipoEntrega == valor) Color(0xFF5B8DEF) else Color.Black
                    )
                }
            }
        }
    }
}

//Funcion para aplicar un color dependiendo del estado de la sesion
private fun colorEstado(estado: String): Color = when (estado) {
    "pendiente" -> Color(0xFFF9A825)
    "confirmada" -> Color(0xFF42A5F5)
    "realizada" -> Color(0xFF1E88E5)
    "en_edicion" -> Color(0xFF5C6BC0)
    "lista_entrega" -> Color(0xFF7E57C2)
    "entregada" -> Color(0xFF66BB6A)
    "rechazada" -> Color(0xFFEF5350)
    "cancelada" -> Color(0xFF9E9E9E)
    else -> Color.Gray
}

//Funcion de formateo de fecha que se aplica tambien en la pantalla
fun formatearFecha(fecha: String): String {
    return try {
        val soloFecha = fecha.take(10)
        val partes = soloFecha.split("-")

        if (partes.size == 3) {
            "${partes[2]}/${partes[1]}/${partes[0]}"
        } else {
            fecha
        }
    } catch (e: Exception) {
        fecha
    }
}