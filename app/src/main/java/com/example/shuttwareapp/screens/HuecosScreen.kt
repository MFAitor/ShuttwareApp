package com.example.shuttwareapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shuttwareapp.R
import com.example.shuttwareapp.components.TarjetaHueco
import com.example.shuttwareapp.model.Hueco
import com.example.shuttwareapp.viewmodel.HuecosViewModel
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HuecosScreen(
    idUsuario: Int,
    viewModel: HuecosViewModel = viewModel()
) {
    val huecos by viewModel.huecos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    var mesSeleccionado by remember { mutableStateOf(LocalDate.now().monthValue) }
    var huecoSeleccionado by remember { mutableStateOf<Hueco?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    //Context y locale para cambiar los dias de los meses
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]

    //Meses que tienen huecos libres (ordenados por anio y mes)
    val meses = remember(huecos) {
        huecos
            .map {
                val fecha = OffsetDateTime.parse(it.fecha)
                    .atZoneSameInstant(ZoneId.of("Europe/Madrid"))
                    .toLocalDate()
                val mes = fecha.monthValue
                val año = fecha.year
                Triple(año, mes, fecha.month.getDisplayName(TextStyle.SHORT, locale).uppercase())
            }
            .distinct()
            .sortedWith(compareBy({ it.first }, { it.second }))
            .map { it.second to it.third }
    }

    // Recarga los huecos cada vez que la pantalla se vuelve a mostrar
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.cargarHuecos()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    //Fondo con el color con degradado
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
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.que_d_a_te_encaja_lo_miramos),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 15.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.h_u_e_c_o_s),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(16.dp))

            //Selector de meses
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(28.dp))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                meses.forEach { (num, nombre) ->
                    val seleccionado = mesSeleccionado == num
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (seleccionado) Color(0xFF5B8DEF) else Color.Transparent)
                            .clickable { mesSeleccionado = num }
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            nombre,
                            color = if (seleccionado) Color.White else Color.DarkGray,
                            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            val filtrados = huecos.filter {
                val fecha = OffsetDateTime.parse(it.fecha)
                    .atZoneSameInstant(ZoneId.of("Europe/Madrid"))
                    .toLocalDate()
                fecha.monthValue == mesSeleccionado && it.estado == "libre"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.disponibles), color = Color.White.copy(0.7f), fontSize = 13.sp)
                Text(stringResource(R.string.d_as, filtrados.size), color = Color.White.copy(0.7f), fontSize = 13.sp)
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filtrados) { hueco ->
                    TarjetaHueco(hueco) { huecoSeleccionado = hueco }
                }
            }
        }

        //Mensajes emergentes
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    //Desplegable para solicitar el hueco seleccionado
    huecoSeleccionado?.let { h ->
        SolicitarHuecoSheet(
            hueco = h,
            onDismiss = { huecoSeleccionado = null },
            onSolicitar = { tipo, lugar, nota, tipoEntrega ->
                viewModel.solicitarHueco(
                    hueco = h,
                    tipo = tipo,
                    lugar = lugar,
                    nota = nota,
                    idUsuario = idUsuario,
                    tipoEntrega = tipoEntrega
                )
                huecoSeleccionado = null
            }
        )
    }

    //Mostrar mensaje de exito o error
    mensaje?.let { texto ->
        LaunchedEffect(texto) {
            snackbarHostState.showSnackbar(texto)
            viewModel.limpiarMensaje()
        }
    }
}