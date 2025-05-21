package com.example.notimedi

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.data.NotiMediDatabase
import com.example.notimedi.data.model.Medicamento
import com.example.notimedi.data.model.preferences.UserPreferences
import com.example.notimedi.notificaciones.RecordatorioWorker
import com.example.notimedi.ui.theme.NotiMediTheme
import kotlinx.coroutines.launch
import java.util.*
import androidx.work.*
import java.util.concurrent.TimeUnit

class EstablecerRecordatorioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearCanalNotificaciones(this)

        val perfilId = intent.getIntExtra("perfilId", -1)
        val medicamentoId = intent.getIntExtra("medicamentoId", -1)

        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EstablecerRecordatorioScreen(perfilId = perfilId, medicamentoId = medicamentoId)
                }
            }
        }
    }
}

fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val canal = NotificationChannel(
            "recordatorios_channel",
            "Recordatorios",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones para recordatorios de medicamentos"
            setSound(sonido, null)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(canal)
    }
}


fun programarRecordatoriosConWorkManager(
    context: Context,
    medicamentoId: Int,
    nombre: String,
    dosis: String,
    unidad: String,
    via: String,
    hora: Int,
    minuto: Int,
    frecuenciaHoras: Int
) {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hora)
        set(Calendar.MINUTE, minuto)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val delayInMillis = target.timeInMillis - now.timeInMillis
    val delay = delayInMillis / 1000  // en segundos

    val datos = workDataOf(
        "nombre" to nombre,
        "dosis" to dosis,
        "unidad" to unidad,
        "via" to via
    )

    val request = PeriodicWorkRequestBuilder<RecordatorioWorker>(
        frecuenciaHoras.toLong(), TimeUnit.HOURS
    )
        .setInitialDelay(delay, TimeUnit.SECONDS)
        .setInputData(datos)
        .addTag("medicamento_$medicamentoId")
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "medicamento_$medicamentoId",
        ExistingPeriodicWorkPolicy.REPLACE,
        request
    )

    Toast.makeText(
        context,
        "✅ Recordatorio programado cada $frecuenciaHoras horas desde las ${"%02d:%02d".format(hora, minuto)}",
        Toast.LENGTH_SHORT
    ).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstablecerRecordatorioScreen(perfilId: Int, medicamentoId: Int = -1) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = NotiMediDatabase.getDatabase(context)
    val userPrefs = remember { UserPreferences(context) }
    val currentUser by userPrefs.getCurrentUser().collectAsState(initial = "")

    var horaSeleccionada by remember { mutableStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var minutoSeleccionado by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
    var mostrarTimePicker by remember { mutableStateOf(false) }

    var nombreMedicamento by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    var unidadSeleccionada by remember { mutableStateOf("mg") }
    var viaSeleccionada by remember { mutableStateOf("Oral") }
    var unidadExpanded by remember { mutableStateOf(false) }
    var viaExpanded by remember { mutableStateOf(false) }

    val alergias by produceState(initialValue = emptyList<String>()) {
        value = db.alergiaDao().obtenerPorPerfil(perfilId).map { it.nombre.lowercase() }
    }

    if (mostrarTimePicker) {
        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                horaSeleccionada = hour
                minutoSeleccionado = minute
                mostrarTimePicker = false
            },
            horaSeleccionada,
            minutoSeleccionado,
            false
        ).show()
    }

    LaunchedEffect(medicamentoId) {
        if (medicamentoId != -1) {
            val med = db.medicamentoDao().obtenerPorId(medicamentoId)
            med?.let {
                nombreMedicamento = it.nombre
                dosis = it.dosisMg.toString()
                frecuencia = it.frecuenciaHoras.toString()
                unidadSeleccionada = it.unidad
                viaSeleccionada = it.via

                // Esto es para la hora
                val (horaParte, ampmParte) = it.hora.split(" ")
                val (hStr, mStr) = horaParte.split(":")
                val h = hStr.toInt()
                val m = mStr.toInt()
                horaSeleccionada = if (ampmParte == "PM" && h != 12) h + 12 else if (ampmParte == "AM" && h == 12) 0 else h
                minutoSeleccionado = m
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecciona la hora", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { mostrarTimePicker = true }) {
            Text("Hora seleccionada: %02d:%02d".format(horaSeleccionada, minutoSeleccionado))
        }
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombreMedicamento,
            onValueChange = { nombreMedicamento = it },
            label = { Text("Nombre del medicamento") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dosis,
            onValueChange = { if (it.all { char -> char.isDigit() }) dosis = it },
            label = { Text("Dosis") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = frecuencia,
            onValueChange = { if (it.all { char -> char.isDigit() }) frecuencia = it },
            label = { Text("Frecuencia (horas)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = unidadExpanded, onExpandedChange = { unidadExpanded = !unidadExpanded }) {
            OutlinedTextField(
                value = unidadSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Unidad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = unidadExpanded, onDismissRequest = { unidadExpanded = false }) {
                listOf("mg", "ml", "gotas", "tabletas").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = {
                        unidadSeleccionada = it
                        unidadExpanded = false
                    })
                }
            }
        }

        ExposedDropdownMenuBox(expanded = viaExpanded, onExpandedChange = { viaExpanded = !viaExpanded }) {
            OutlinedTextField(
                value = viaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vía de administración") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = viaExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = viaExpanded, onDismissRequest = { viaExpanded = false }) {
                listOf("Oral", "Tópica", "Intramuscular", "Intradérmica").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = {
                        viaSeleccionada = it
                        viaExpanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (nombreMedicamento.isBlank() || dosis.isBlank() || frecuencia.isBlank()) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (alergias.contains(nombreMedicamento.trim().lowercase())) {
                Toast.makeText(context, "Este medicamento está en la lista de alergias del perfil", Toast.LENGTH_LONG).show()
                return@Button
            }
            val frecuenciaHoras = frecuencia.toIntOrNull() ?: 0
            if (frecuenciaHoras !in 1..24) {
                Toast.makeText(context, "La frecuencia debe ser entre 1 y 24 horas", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val ampmSeleccionado = if (horaSeleccionada < 12) "AM" else "PM"
            val hora12 = if (horaSeleccionada % 12 == 0) 12 else horaSeleccionada % 12
            val horaFinal = "%02d:%02d %s".format(hora12, minutoSeleccionado, ampmSeleccionado)

            scope.launch {
                val nuevoMed = Medicamento(
                    id = if (medicamentoId != -1) medicamentoId else 0,
                    perfilId = perfilId,
                    nombre = nombreMedicamento,
                    dosisMg = dosis.toInt(),
                    via = viaSeleccionada,
                    hora = horaFinal,
                    unidad = unidadSeleccionada,
                    dias = "",
                    frecuenciaHoras = frecuenciaHoras,
                    username = currentUser ?: ""
                )

                val medFinal = if (medicamentoId != -1) {
                    db.medicamentoDao().actualizar(nuevoMed)
                    nuevoMed
                } else {
                    val id = db.medicamentoDao().insertar(nuevoMed).toInt()
                    nuevoMed.copy(id = id)
                }

                programarRecordatoriosConWorkManager(
                    context = context,
                    medicamentoId = medFinal.id,
                    nombre = medFinal.nombre,
                    dosis = medFinal.dosisMg.toString(),
                    unidad = medFinal.unidad,
                    via = medFinal.via,
                    hora = horaSeleccionada,
                    minuto = minutoSeleccionado,
                    frecuenciaHoras = medFinal.frecuenciaHoras
                )

                Toast.makeText(context, "Recordatorio guardado", Toast.LENGTH_SHORT).show()
                (context as? EstablecerRecordatorioActivity)?.finish()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar recordatorio")
        }
    }
}





