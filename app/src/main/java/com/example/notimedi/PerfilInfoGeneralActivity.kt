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
import android.graphics.Bitmap
import com.example.notimedi.data.model.preferences.UserPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import androidx.core.content.FileProvider
import java.io.File
import android.os.Environment
import java.io.FileOutputStream
import android.graphics.pdf.PdfDocument
import android.graphics.BitmapFactory
import androidx.core.content.res.ResourcesCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                try {
                    val nombrePerfil = perfil?.nombre ?: "Perfil"
                    val pdfFile = generatePdf(context, nombrePerfil, perfil, medicamentos, alergias)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "com.example.notimedi.provider",
                        pdfFile
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Error al generar PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2540)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Compartir información del perfil", color = Color.White)
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

fun generatePdf(
    context: android.content.Context,
    nombre: String,
    perfil: Perfil?,
    medicamentos: List<Medicamento>,
    alergias: List<Alergia>
): File {
    val logo = BitmapFactory.decodeResource(context.resources, R.drawable.notimedilogo)
    val pdfDocument = PdfDocument()
    val pageHeight = 900
    val pageWidth = 300

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaHora = dateFormat.format(Date())

    var pageNumber = 1
    var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas
    val paint = android.graphics.Paint()
    paint.textSize = 12f

    var y = 20

    fun drawFooter() {
        val logoSize = 20
        val centerX = pageWidth / 2
        val text = "NotiMedi"
        val textWidth = paint.measureText(text)
        val textX = centerX - (textWidth / 2)
        val logoX = centerX - (logoSize / 2)
        val footerY = pageHeight - 25f

        // Fecha y hora de generación encima del logo
        val fechaWidth = paint.measureText(fechaHora)
        val fechaX = centerX - (fechaWidth / 2)
        canvas.drawText(fechaHora, fechaX, footerY - 25f, paint)

        // Nombre y logo
        canvas.drawText(text, textX, footerY, paint)
        canvas.drawBitmap(Bitmap.createScaledBitmap(logo, logoSize, logoSize, true), logoX.toFloat(), footerY - 20f, null)
    }

    fun checkPageOverflow(): Boolean {
        return y > pageHeight - 80
    }

    fun nuevaPagina() {
        drawFooter()
        pdfDocument.finishPage(page)
        pageNumber++
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas
        y = 20
    }

    canvas.drawText("Perfil de: $nombre", 10f, y.toFloat(), paint)
    y += 20

    perfil?.let {
        if (checkPageOverflow()) nuevaPagina()
        canvas.drawText("Sexo: ${it.sexo}", 10f, y.toFloat(), paint)
        y += 20
        if (checkPageOverflow()) nuevaPagina()
        canvas.drawText("Nacimiento: ${it.fechaNacimiento}", 10f, y.toFloat(), paint)
        y += 20
    }

    if (checkPageOverflow()) nuevaPagina()
    canvas.drawText("Medicamentos:", 10f, y.toFloat(), paint)
    y += 20

    if (medicamentos.isEmpty()) {
        if (checkPageOverflow()) nuevaPagina()
        canvas.drawText("  - Ninguno registrado", 10f, y.toFloat(), paint)
        y += 20
    } else {
        medicamentos.forEach {
            if (checkPageOverflow()) nuevaPagina()
            canvas.drawText("  - ${it.nombre}, ${it.dosisMg}${it.unidad}, ${it.hora}, ${it.via}", 10f, y.toFloat(), paint)
            y += 20
        }
    }

    if (checkPageOverflow()) nuevaPagina()
    canvas.drawText("Alergias:", 10f, y.toFloat(), paint)
    y += 20

    if (alergias.isEmpty()) {
        if (checkPageOverflow()) nuevaPagina()
        canvas.drawText("  - Ninguna", 10f, y.toFloat(), paint)
        y += 20
    } else {
        alergias.forEach {
            if (checkPageOverflow()) nuevaPagina()
            canvas.drawText("  - ${it.nombre}", 10f, y.toFloat(), paint)
            y += 20
        }
    }

    drawFooter()
    pdfDocument.finishPage(page)

    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Perfil_$nombre.pdf")
    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()
    return file

    //Que viva Chayanne
}

