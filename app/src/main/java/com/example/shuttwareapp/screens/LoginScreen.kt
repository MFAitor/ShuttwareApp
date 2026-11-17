package com.example.shuttwareapp.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shuttwareapp.R
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.viewmodel.AuthViewModel
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.shuttwareapp.utils.IdiomaManager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding

@Composable
fun LoginScreen(
    apiService: ApiService,
    authViewModel: AuthViewModel,
    onLoginOk: () -> Unit,
    onRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    //Variable para gestionar los permisos del sistema
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

    }

    val context = LocalContext.current

    var mostrarDialogoRecuperacion by remember { mutableStateOf(false) }
    //Variable para guardar el idioma seleccionado
    var idiomaActual by remember { mutableStateOf(IdiomaManager.obtener(context)) }

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
        //Selector de idioma
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Parte de selector de idioma que reacciona a la interaccion del usuario
                Text(
                    text = "ES",
                    color = if (idiomaActual == "es") Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontWeight = if (idiomaActual == "es") FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable {
                        if (idiomaActual != "es") {
                            idiomaActual = "es"
                            IdiomaManager.guardar(context, "es")
                            IdiomaManager.aplicar(context, "es")
                            (context as? android.app.Activity)?.recreate()
                        }
                    }
                )

                Text(
                    text = "|",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )

                Text(
                    text = "GL",
                    color = if (idiomaActual == "gl") Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontWeight = if (idiomaActual == "gl") FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable {
                        if (idiomaActual != "gl") {
                            idiomaActual = "gl"
                            IdiomaManager.guardar(context, "gl")
                            IdiomaManager.aplicar(context, "gl")
                            (context as? android.app.Activity)?.recreate()
                        }
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()                    // ← deja espacio para el teclado
                .verticalScroll(rememberScrollState())  // ← permite hacer scroll
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.shuttware_logo_no_brackground),
                contentDescription = "Logo Shuttware",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.s_h_u_t_t_w_a_r_e),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6F4))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.a_c_c_e_d_e_r),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 6.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    //Introduccion de el campo email
                    Text(stringResource(R.string.email), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.contrase_a), fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    //Introduccion de la contrasenia
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
                                    if (passwordVisible) stringResource(R.string.ocultar) else stringResource(
                                        R.string.mostrar
                                    ),
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    //Enlace para abrir el dialogo de recuperacion
                    Text(
                        text = stringResource(R.string.olvidaste_tu_contrase_a),
                        color = Color(0xFF4A90E2),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { mostrarDialogoRecuperacion = true }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            authViewModel.login(
                                context = context,
                                apiService = apiService,
                                email = email,
                                password = password,
                                onSuccess = {
                                    //Pedir permiso de notificaciones
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }

                                    onLoginOk()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        Text(stringResource(R.string.iniciar_sesi_n), fontSize = 16.sp)
                    }

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
                        //Mensaje para que el usuario se registre si no tiene cuenta
                        Text(stringResource(R.string.a_n_no_tienes_cuenta), color = Color.Gray, fontSize = 13.sp)
                        Text(
                            text = stringResource(R.string.reg_strate),
                            color = Color(0xFF4A90E2),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onRegister() }
                        )
                    }
                }
            }
        }
    }

    //Dialogo que indica que el fotografo resetea la contrasenia
    if (mostrarDialogoRecuperacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRecuperacion = false },
            title = { Text(stringResource(R.string.has_olvidado_tu_contrase_a)) },
            text = {
                Text(
                    stringResource(R.string.ponte_en_contacto_con_tu_fot_grafo_para_solicitar_la_recuperaci_n_de_tu_contrase_a)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { mostrarDialogoRecuperacion = false }
                ) {
                    Text(stringResource(R.string.entendido))
                }
            }
        )
    }
}