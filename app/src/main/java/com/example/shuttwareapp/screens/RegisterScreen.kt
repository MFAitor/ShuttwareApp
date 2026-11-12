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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.R
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    apiService: ApiService,
    authViewModel: AuthViewModel,
    onRegisterOk: () -> Unit,
    onLogin: () -> Unit
) {
    //Variables para ingresar datos nuevos de registro
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

            // Cabecera
            Text(
                text = stringResource(R.string.bienvenido_a_shuttware),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
            Text(
                text = stringResource(R.string.c_r_e_a_r_c_u_e_n_t_a),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Tarjeta de registro
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6F4))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    //Nombre y Apellidos en la misma fila
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.nombre), fontSize = 13.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                placeholder = { Text(stringResource(R.string.ana)) }
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Apellidos", fontSize = 13.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = apellidos,
                                onValueChange = { apellidos = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                placeholder = { Text(stringResource(R.string.l_pez_ruiz)) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.email), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.ana_correo_com)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.contrase_a), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    if (passwordVisible) stringResource(R.string.ocultar) else stringResource(R.string.mostrar) ,
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.m_nimo_8_caracteres),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    //Separador opcional
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.opcional),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = stringResource(R.string.puedes_a_adirlo_luego),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .height(1.dp)
                                .weight(1f)
                                .background(Color.LightGray)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.tel_fono), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string._34_600_000_000)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.instagram), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = instagram,
                        onValueChange = { instagram = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.tu_usuario)) }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    //Botón crear cuenta
                    Button(
                        //Al pulsar el boton comprobamos los datos
                        onClick = {
                            if (nombre.isBlank() || email.isBlank() || password.isBlank()) return@Button

                            //Y se llama al metodo para registrar usuario nuevo
                            authViewModel.registro(
                                context = context,
                                apiService = apiService,
                                nombre = nombre,
                                apellidos = apellidos,
                                email = email,
                                password = password,
                                telefono = telefono.ifBlank { null },
                                instagram = instagram.ifBlank { null },
                                onSuccess = onRegisterOk
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        Text(stringResource(R.string.crea_cuenta), fontSize = 16.sp)
                    }

                    // Mostrar error si hay
                    if (authViewModel.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = authViewModel.error!!,
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.ya_tienes_cuenta), color = Color.Gray, fontSize = 13.sp)
                        Text(
                            text = stringResource(R.string.inicia_sesi_n),
                            color = Color(0xFF4A90E2),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onLogin() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}