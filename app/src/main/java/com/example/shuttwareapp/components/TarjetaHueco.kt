package com.example.shuttwareapp.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.model.Hueco
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

//Tarjeta en la que se mostrara cada uno de los huecos disponibles para pedir
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TarjetaHueco(hueco: Hueco, onClick: () -> Unit) {

    //Guardo context y cambio el "locale" en funcion del idioma
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]

    //Parseamos la fecha que viene como String (ajustada a España)
    val fechaLocal = OffsetDateTime.parse(hueco.fecha)
        .atZoneSameInstant(ZoneId.of("Europe/Madrid"))
        .toLocalDate()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            //Día de la semana en espaniol
            Text(
                text = fechaLocal.dayOfWeek
                    .getDisplayName(TextStyle.FULL, locale)
                    .replaceFirstChar { it.uppercase() },
                color = Color.Gray,
                fontSize = 13.sp
            )
            //Formateo de fecha tipo "15 de marzo"
            Text(
                text = "${fechaLocal.dayOfMonth} de ${
                    fechaLocal.month
                        .getDisplayName(TextStyle.FULL, locale)
                        .replaceFirstChar { it.uppercase() }
                }",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }

        Text(
            text = "›",
            fontSize = 24.sp,
            color = Color.Gray
        )
    }
}