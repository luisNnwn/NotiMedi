package com.example.notimedi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.example.notimedi.data.NotiMediDatabase
import com.example.notimedi.data.model.Medicamento
import com.example.notimedi.notificaciones.RecordatorioWorker
import com.example.notimedi.ui.theme.NotiMediTheme
import kotlinx.coroutines.launch
import com.example.notimedi.data.model.preferences.UserPreferences

class ListaMedicamentosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val perfilId = intent.getIntExtra("perfilId", -1)

        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ListaMedicamentosScreen(perfilId)
                }
            }
        }
    }
}

@Composable
fun ListaMedicamentosScreen(perfilId: Int) {
    val context = LocalContext.current
    val db = remember { NotiMediDatabase.getDatabase(context) }
    val userPrefs = remember { UserPreferences(context) }
    val currentUser = userPrefs.getCurrentUser().collectAsState(initial = "").value ?: ""
    val scope = rememberCoroutineScope()

    val medicamentos by db.medicamentoDao()
        .obtenerPorPerfilYUsuario(perfilId, currentUser)
        .collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, EstablecerRecordatorioActivity::class.java)
                    intent.putExtra("perfilId", perfilId)
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF5D7FBF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Medicamentos programados", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            if (medicamentos.isEmpty()) {
                Text("No hay medicamentos agregados aún.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(medicamentos) { medicamento ->
                        MedicamentoCard(
                            medicamento = medicamento,
                            onEliminar = {
                                scope.launch {
                                    cancelarRecordatorioConWorkManager(context, medicamento)
                                    db.medicamentoDao().eliminar(medicamento)
                                    Toast.makeText(context, "Recordatorio eliminado", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEditar = {
                                val intent = Intent(context, EstablecerRecordatorioActivity::class.java).apply {
                                    putExtra("perfilId", perfilId)
                                    putExtra("medicamentoId", medicamento.id)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MedicamentoCard(
    medicamento: Medicamento,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2A49))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(medicamento.nombre, style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${medicamento.dosisMg} ${medicamento.unidad} – ${medicamento.via}", color = Color.White)
            Text("Hora: ${medicamento.hora}", color = Color.White)
            Text("Días: ${medicamento.dias}", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onEditar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar recordatorio", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onEliminar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancelar notificaciones", color = Color.White)
            }
        }
    }
}


fun cancelarRecordatorioConWorkManager(context: Context, medicamento: Medicamento) {
    WorkManager.getInstance(context).cancelUniqueWork("medicamento_${medicamento.id}")
}
