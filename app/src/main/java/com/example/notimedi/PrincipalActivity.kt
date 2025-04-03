package com.example.notimedi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

class PrincipalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PrincipalScreen()
                }
            }
        }
    }
}

@Composable
fun PrincipalScreen() {
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2B3D66),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Ya estás en Principal */ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.casa),
                            contentDescription = "Inicio",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    },
                    label = {
                        Text("Inicio", color = Color.White)
                    }
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
                    label = {
                        Text("Perfiles", color = Color.White)
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        context.startActivity(Intent(context, MedicamentosActivity::class.java))
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pastilla),
                            contentDescription = "Medicamentos",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    },
                    label = {
                        Text("Medicamentos", color = Color.White)
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notimedilogo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(64.dp)
                )
                IconButton(onClick = {
                    val intent = Intent(context, ConfiguracionActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Settings, contentDescription = "Configuración")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hola, Chayanne.\nBienvenido a NotiMedi, desde acá puedes empezar a crear tu itinerario de medicamentos",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )

            Button(
                onClick = {
                    val intent = Intent(context, PerfilesActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF))
            ) {
                Text("Crear mi itinerario", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Infórmate!",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Y siempre que quieras, puedes leer estos artículos informativos.",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val articles = listOf(
                "Importancia de tomar los medicamentos a tiempo" to "https://www.youtube.com/watch?v=1",
                "¿Puedo tomar alcohol con mi medicamento?" to "https://www.youtube.com/watch?v=2",
                "Consejos para organizar tus medicamentos" to "https://www.youtube.com/watch?v=3",
                "Evita errores comunes al tomar tus medicinas" to "https://www.youtube.com/watch?v=4",
                "Tipos de medicamentos y sus efectos" to "https://www.youtube.com/watch?v=5"
            )

            articles.forEach { (title, url) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F6FB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "Haz clic para ver más información en video",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}
