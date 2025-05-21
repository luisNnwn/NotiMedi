package com.example.notimedi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.notimedi.data.NotiMediDatabase
import com.example.notimedi.data.model.Medicamento
import com.example.notimedi.data.model.Alergia
import com.example.notimedi.data.model.Perfil
import com.example.notimedi.ui.theme.NotiMediTheme
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.Icons
import android.content.Intent
import com.example.notimedi.data.model.preferences.UserPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class PerfilInfoGeneralActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val perfilId = intent.getIntExtra("perfilId", -1)

        val userPrefs = UserPreferences(this)
        val currentUser = runBlocking { userPrefs.getCurrentUser().first() ?: "" }

        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PerfilInfoGeneralScreen(perfilId = perfilId, username = currentUser)
                }
            }
        }
    }
}

@Composable
fun PerfilInfoGeneralScreen(perfilId: Int, username: String) {
    val context = LocalContext.current
    val db = remember { NotiMediDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var perfil by remember { mutableStateOf<Perfil?>(null) }
    var medicamentos by remember { mutableStateOf(listOf<Medicamento>()) }
    var alergias by remember { mutableStateOf(listOf<Alergia>()) }
    var expandedSection by remember { mutableStateOf<String?>(null) }

    fun cargarDatosPerfil() {
        scope.launch {
            perfil = db.perfilDao().obtenerPorId(perfilId)
            medicamentos = db.medicamentoDao().obtenerTodos().filter { it.perfilId == perfilId && it.username == username }
            alergias = db.alergiaDao().obtenerPorPerfil(perfilId).filter { it.username == username }
        }
    }

    LaunchedEffect(perfilId) {
        cargarDatosPerfil()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                cargarDatosPerfil()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(perfil?.nombre ?: "Perfil", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(context, ListaMedicamentosActivity::class.java).apply {
                    putExtra("perfilId", perfilId)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF))
        ) {
            Text("Crear mi itinerario", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExpansionTile(
            title = "Información Personal",
            isExpanded = expandedSection == "info",
            onClick = {
                expandedSection = if (expandedSection == "info") null else "info"
            }
        ) {
            perfil?.let {
                Text("Nombre: ${it.nombre}")
                Text("Sexo: ${it.sexo}")
                Text("Nacimiento: ${it.fechaNacimiento}")
            }
        }

        ExpansionTile(
            title = "Medicamentos",
            isExpanded = expandedSection == "meds",
            onClick = {
                expandedSection = if (expandedSection == "meds") null else "meds"
            }
        ) {
            if (medicamentos.isEmpty()) {
                Text("No hay medicamentos registrados")
            } else {
                medicamentos.forEach {
                    Text("• ${it.nombre} - ${it.dosisMg}${it.unidad}, ${it.via}, ${it.hora} (${it.dias})")
                }
            }
        }

        ExpansionTile(
            title = "Alergias",
            isExpanded = expandedSection == "alergias",
            onClick = {
                expandedSection = if (expandedSection == "alergias") null else "alergias"
            }
        ) {
            if (alergias.isEmpty()) {
                Text("Sin alergias registradas")
            } else {
                alergias.forEach { Text("• ${it.nombre}") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Función de compartir próximamente", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5A80))
        ) {
            Icon(
                imageVector = Icons.Filled.PictureAsPdf,
                contentDescription = "PDF",
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Compartir información", color = Color.White)
        }
    }
}

@Composable
fun ExpansionTile(title: String, isExpanded: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2540))
        ) {
            Text(title, color = Color.White)
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                content()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
