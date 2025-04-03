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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.ui.theme.NotiMediTheme

class PerfilInfoGeneralActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PerfilInfoGeneralScreen(
                        nombre = intent.getStringExtra("nombre") ?: "Nombre"
                    )
                }
            }
        }
    }
}

@Composable
fun PerfilInfoGeneralScreen(nombre: String) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_leftarrow),
                contentDescription = "Volver",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        (context as? ComponentActivity)?.finish()
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        listOf(
            "Información Personal",
            "Lista de medicamentos",
            "Alergias"
        ).forEach { label ->
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2540)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = label, color = Color.White)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Button(
            onClick = {
                val message = "Perfil de $nombre\n- Información Personal: ...\n- Medicamentos: ...\n- Alergias: ..."
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2540)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir información del perfil", color = Color.White)
        }
    }
}

