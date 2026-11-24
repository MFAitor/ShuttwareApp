package com.example.shuttwareapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.model.Hueco
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.shuttwareapp.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarHuecoSheet(
    hueco: Hueco,
    onDismiss: () -> Unit,
    onSolicitar: (tipo: String, lugar: String, nota: String, tipoEntrega: String?) -> Unit
) {
    var tipo by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var tipoEntrega by remember { mutableStateOf<String?>(null) }

    //Variables para las opciones de seleccion de tipo de entrega
    val opcionesEntrega = listOf(
        "link_descarga" to stringResource(R.string.link_de_descarga),
        "USB" to stringResource(R.string.usb),
        "album" to stringResource(R.string.album),
        "caja_personalizada" to stringResource(R.string.caja_personalizada)
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    //Variables para la traduccion de las fechas
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.solicitar_sesi_n),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Formatear la fecha que llega como String ISO
            val fechaFormateada = try {
                val instant = java.time.Instant.parse(hueco.fecha)
                val localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                localDate.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", locale))
                    .replaceFirstChar { it.uppercase() }
            } catch (e: Exception) {
                hueco.fecha // fallback por si falla el parseo
            }

            Text(
                text = fechaFormateada,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5B8DEF)
            )

            Spacer(Modifier.height(24.dp))

            Text(stringResource(R.string.tipo_de_sesi_n), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.retrato_boda_producto)) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.lugar_de_preferencia), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = lugar,
                onValueChange = { lugar = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.playa_estudio_exterior)) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            //Botones para elegir el tipo de entrega que se va a realizar
            Text(stringResource(R.string.tipo_de_entrega_opcional), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                opcionesEntrega.forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = tipoEntrega == valor,
                        onClick = { tipoEntrega = if (tipoEntrega == valor) null else valor },
                        label = {
                            Text(
                                etiqueta,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.nota_para_el_fot_grafo_opcional), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text(stringResource(R.string.cu_ntale_qu_tienes_en_mente)) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { onSolicitar(tipo, lugar, nota, tipoEntrega) },
                enabled = tipo.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B8DEF))
            ) {
                Text(stringResource(R.string.solicitar_sesi_n), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Text(
                stringResource(R.string.el_fot_grafo_confirmar_tu_solicitud),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }
}