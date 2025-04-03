package com.example.notimedi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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

class ConfiguracionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                text = "Ajustes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { /* Cambiar contraseña */ },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2C2C))
            ) {
                Text("Cambiar contraseña", color = Color.White)
            }

            Button(
                onClick = { /* Borrar cuenta */ },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2C2C))
            ) {
                Text("Borrar cuenta", color = Color.White)
            }

            Button(
                onClick = { /* Sobre NotiMedi */ },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2C2C))
            ) {
                Text("Sobre NotiMedi", color = Color.White)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, LoginRegistroActivity::class.java)
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cerrar Sesión", color = Color.White)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName

            Text(
                text = "Versión: $versionName",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
