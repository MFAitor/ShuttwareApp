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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.model.Hueco
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        "link_descarga" to "Link de descarga",
        "USB" to "USB",
        "album" to "Álbum",
        "caja_personalizada" to "Caja personal"
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                "SOLICITAR SESIÓN",
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                hueco.fecha.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es")))
                    .replaceFirstChar { it.uppercase() },
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5B8DEF)
            )

            Spacer(Modifier.height(24.dp))

            Text("Tipo de sesión", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Retrato, boda, producto…") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text("Lugar de preferencia", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = lugar,
                onValueChange = { lugar = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Playa, estudio, exterior…") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            //Botones para elegir el tipo de entrega que se va a realizar
            Text("Tipo de entrega (opcional)", fontWeight = FontWeight.Medium)
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

            Text("Nota para el fotógrafo (opcional)", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Cuéntale qué tienes en mente…") },
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
                Text("Solicitar sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Text(
                "El fotógrafo confirmará tu solicitud",
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