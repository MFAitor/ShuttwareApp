package com.example.shuttwareapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shuttwareapp.R
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.components.TarjetaPreview
import com.example.shuttwareapp.model.Preview
import com.example.shuttwareapp.model.Sesion
import com.example.shuttwareapp.viewmodel.PreviewsViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreviewsScreen(
    sesion: Sesion,
    idUsuario: Int,
    api: ApiService,
    onBack: () -> Unit,
    viewModel: PreviewsViewModel = viewModel()
) {
    val previews = viewModel.previews

    //Cargar las previews al entrar en la pantalla
    LaunchedEffect(sesion.idSesion) {
        viewModel.cargar(api, sesion.idSesion, idUsuario)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B7355),
                        Color(0xFFD4A574),
                        Color(0xFFF5E6D3)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            //Cabecera
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.volver),
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${sesion.tipo ?: stringResource(R.string.sesi_n)} · ${formatearFechaCorta(sesion.fecha)}",
                        color = Color.White.copy(0.8f),
                        fontSize = 13.sp
                    )
                    Text(
                        stringResource(R.string.p_r_e_v_i_e_w_s),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 3.sp
                    )
                }
                Surface(
                    color = Color.White.copy(0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "${previews.size} fotos",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                viewModel.cargando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                viewModel.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(viewModel.error!!, color = Color.White)
                    }
                }

                viewModel.previews.isEmpty() -> {
                    //Mensaje cuando la sesion aun no tiene previews
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.esta_sesi_n_todav_a_no_tiene_previews),
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    //Desplegable de previews
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            .background(Color(0xFFFDF8F3)),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(previews.size) { index ->
                            val preview = previews[index]
                            TarjetaPreview(
                                numero = index + 1,
                                pie = preview.pie ?: "",
                                idPreview = preview.id,
                                baseUrl = "http://172.20.10.3:8080"
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatearFechaCorta(fecha: String): String {
    return try {
        val d = LocalDate.parse(fecha.take(10))
        "${d.dayOfMonth} ${d.month.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))} ${d.year}"
    } catch (e: Exception) {
        fecha.take(10)
    }
}