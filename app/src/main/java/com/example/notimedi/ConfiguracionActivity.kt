package com.example.notimedi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.ui.theme.NotiMediTheme
import com.example.notimedi.data.model.preferences.UserPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.notimedi.util.encriptarPin
import com.example.notimedi.data.NotiMediDatabase

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
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val db = remember { NotiMediDatabase.getDatabase(context) }

    var pinVisible by remember { mutableStateOf(false) }
    var pinText by remember { mutableStateOf("") }
    var showAbout by remember { mutableStateOf(false) }
    val currentUser by userPrefs.getCurrentUser().collectAsState(initial = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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
                        context.startActivity(Intent(context, PrincipalActivity::class.java))
                        (context as? ComponentActivity)?.finish()
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Ajustes", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Sobre NotiMedi con contenido desplegable
            ElevatedButton(
                onClick = { showAbout = !showAbout },
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3A3A))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sobre NotiMedi", color = Color.White)
                    Icon(
                        imageVector = if (showAbout) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            if (showAbout) {
                Text(
                    text = """
                        Desarrolladores:
                        Cinthia Yasmin Rivera Pineda RP101022

                        Emmanuel Antonio Escobar Luna EL100122

                        Raul Antonio Elias Alegria EA100222

                        Luis Nelson Hernandez Blanco HB100122

                        Leonardo Antonio Iraheta Menjivar IM100220

                        Fernando Jose Trejo Guevara TG100221
                    """.trimIndent(),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = { pinVisible = !pinVisible },
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3A3A))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Establecer PIN de seguridad", color = Color.White)
                    Icon(
                        imageVector = if (pinVisible) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            if (pinVisible) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pinText,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) pinText = it
                    },
                    label = { Text("PIN de 6 dígitos") },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                IconButton(onClick = {
                    if (pinText.length == 6 && !currentUser.isNullOrBlank()) {
                        val encriptado = encriptarPin(pinText)
                        scope.launch {
                            db.usuarioDao().actualizarPin(currentUser!!, encriptado)
                            Toast.makeText(context, "PIN establecido", Toast.LENGTH_SHORT).show()
                            pinVisible = false
                            pinText = ""
                        }
                    } else {
                        Toast.makeText(context, "Debe ingresar un PIN válido", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Guardar PIN", tint = Color(0xFF4CAF50))
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Button(
                onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    GoogleSignIn.getClient(context, gso).signOut()

                    scope.launch {
                        userPrefs.clearLoginStatus()
                        val intent = Intent(context, LoginRegistroActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cerrar Sesión", color = Color.White)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            val version = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            Text("Versión: $version", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
