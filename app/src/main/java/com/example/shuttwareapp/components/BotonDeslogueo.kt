package com.example.shuttwareapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.R

//Boton de salir
@Composable
fun BotonSalir(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.salir),
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .clickable { onLogout() }
            .padding(8.dp)
    )
}