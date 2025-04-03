package com.example.notimedi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.ui.theme.NotiMediTheme

class PerfilesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PerfilesScreen()
                }
            }
        }
    }
}

@Composable
fun PerfilesScreen() {
    val context = LocalContext.current
    var perfilCreado by remember { mutableStateOf(false) }
    var perfilAgregado by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var alergias by remember { mutableStateOf("") }
    var perfiles by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2B3D66),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, PrincipalActivity::class.java))
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.casa),
                            contentDescription = "Inicio",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    },
                    label = { Text("Inicio", color = Color.White) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_usuario),
                            contentDescription = "Perfiles",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    },
                    label = { Text("Perfiles", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pastilla),
                            contentDescription = "Medicamentos",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    },
                    label = { Text("Medicamentos", color = Color.White) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_leftarrow),
                    contentDescription = "Volver",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            context.startActivity(Intent(context, PrincipalActivity::class.java))
                            (context as? ComponentActivity)?.finish()
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Perfiles de itinerarios.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when {
                        !perfilCreado -> {
                            Text(
                                text = "Todavía no hay perfiles",
                                color = Color(0xFF1B4D7A),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Crear un perfil es útil si vas a usar NotiMedi por primera vez, y si ya lo haces puedes crear un perfil para algún familiar o amigo del que estés a cargo para darle su medicación a tiempo.",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                            Button(
                                onClick = { perfilCreado = true },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Crear un perfil", color = Color.White)
                            }
                        }

                        !perfilAgregado -> {
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre o apodo") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = sexo,
                                onValueChange = { sexo = it },
                                label = { Text("Sexo") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = fechaNacimiento,
                                onValueChange = { fechaNacimiento = it },
                                label = { Text("Fecha de nacimiento") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = alergias,
                                onValueChange = { alergias = it },
                                label = { Text("Alergias a medicamentos") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    perfiles = perfiles + nombre
                                    perfilAgregado = true
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Agregar perfil", color = Color.White)
                            }
                        }

                        else -> {
                            perfiles.forEach {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            val intent = Intent(context, PerfilInfoGeneralActivity::class.java)
                                            context.startActivity(intent)
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2A49)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = it, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(text = "Perfil Principal", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
