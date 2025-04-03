package com.example.notimedi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.ui.theme.NotiMediTheme

class MedicamentosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MedicamentosScreen()
                }
            }
        }
    }
}

@Composable
fun MedicamentosScreen() {
    val context = LocalContext.current

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
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, PerfilesActivity::class.java))
                    },
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
                    selected = true,
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            context.startActivity(Intent(context, PrincipalActivity::class.java))
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Medicamentos", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_notificacion), // ya lo tenés
                    contentDescription = "Icono notificación",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Esta sección solo es meramente informativa, no se busca recomendar la administración de ningún medicamento.\nEvita automedicarte.",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar medicamento") },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lupa),
                        contentDescription = "Buscar"
                    )
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_lupainicial),
                contentDescription = "Buscar icono",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "¿Quieres saber algunas cosas sobre los medicamentos que tomas?",
                textAlign = TextAlign.Center
            )
        }
    }
}


