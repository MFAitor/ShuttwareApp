package com.example.shuttwareapp.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    apiService: ApiService,
    perfilViewModel: PerfilViewModel,
    idUsuario: Int,
    onLogout: () -> Unit
) {
    LaunchedEffect(idUsuario) {
        perfilViewModel.cargarPerfil(apiService, idUsuario)
    }

    var error = perfilViewModel.error
    val mensaje = perfilViewModel.mensaje

    val cliente = perfilViewModel.perfil

    if (perfilViewModel.cargandoPerfil || cliente == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val nombre = cliente["nombre"] as? String ?: ""
    val apellidos = cliente["apellidos"] as? String ?: ""
    val email = cliente["email"] as? String ?: ""

    //El telefono llega como Int por lo que hay que convertirlo a String
    val telefono = when (val t = cliente["telefono"]) {
        is Number -> t.toLong().toString()
        else -> t?.toString() ?: ""
    }

    val instagram = cliente["instagram"] as? String ?: ""

    var nombreEdit by remember(nombre) { mutableStateOf(nombre) }
    var apellidosEdit by remember(apellidos) { mutableStateOf(apellidos) }
    var telefonoEdit by remember(telefono) { mutableStateOf(telefono) }
    var instagramEdit by remember(instagram) { mutableStateOf(instagram) }

    var mostrarCambioPass by remember { mutableStateOf(false) }

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
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombre.take(1).uppercase() + apellidos.take(1).uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF344B66)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cliente desde sep 2026",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Tarjeta de datos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6F4))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "DATOS PERSONALES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    CampoEditable("Nombre", nombreEdit) { nombreEdit = it }
                    CampoEditable("Apellidos", apellidosEdit) { apellidosEdit = it }
                    CampoEditable("Email", email, enabled = false) {}
                    CampoEditable("Teléfono", telefonoEdit) { telefonoEdit = it }
                    CampoEditable("Instagram", instagramEdit) { instagramEdit = it }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "SEGURIDAD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    //Fila de contrasenia
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Contraseña", modifier = Modifier.weight(1f))
                        Text("••••••", color = Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Cambiar ›",
                            color = Color(0xFF4A90E2),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { mostrarCambioPass = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    //Mensaje de error
                    if (error != null) {
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    //Mensaje de exito
                    if (mensaje != null) {
                        Text(
                            text = mensaje,
                            color = Color(0xFF2E7D32),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val sinCambios = nombreEdit == nombre &&
                                    apellidosEdit == apellidos &&
                                    telefonoEdit == telefono &&
                                    instagramEdit == instagram

                            if (sinCambios) {
                                perfilViewModel.error = "No se ha modificado el perfil"
                            } else {
                                perfilViewModel.guardarPerfil(
                                    apiService = apiService,
                                    id = idUsuario,
                                    nombre = nombreEdit,
                                    apellidos = apellidosEdit,
                                    telefono = telefonoEdit,
                                    instagram = instagramEdit,
                                    onSuccess = { }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        Text("Guardar cambios", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Cerrar sesión",
                        color = Color(0xFFE53935),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onLogout() }
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    //Dialogo de cambio de contrasenia
    if (mostrarCambioPass) {
        DialogoCambiarContrasena(
            onDismiss = { mostrarCambioPass = false },
            onConfirm = { actual, nueva ->
                perfilViewModel.cambiarContrasena(apiService, idUsuario, actual, nueva) {
                    mostrarCambioPass = false
                }
            }
        )
    }
}

@Composable
private fun CampoEditable(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
private fun DialogoCambiarContrasena(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var repetir by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Cambiar la contraseña?") },
        text = {
            Column {
                Text(
                    "Usarás la nueva la próxima vez que inicies sesión.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Contraseña actual", fontSize = 13.sp)
                OutlinedTextField(
                    value = actual,
                    onValueChange = { actual = it },
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { visible = !visible }) {
                            Text(if (visible) "Ocultar" else "Mostrar", fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Nueva contraseña", fontSize = 13.sp)
                OutlinedTextField(
                    value = nueva,
                    onValueChange = { nueva = it },
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Repetir nueva contraseña", fontSize = 13.sp)
                OutlinedTextField(
                    value = repetir,
                    onValueChange = { repetir = it },
                    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nueva == repetir && nueva.isNotBlank()) {
                        onConfirm(actual, nueva)
                    }
                }
            ) {
                Text("Sí, cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}