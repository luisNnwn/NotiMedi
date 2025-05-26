package com.example.notimedi

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
import com.example.notimedi.ui.navigation.BottomBarNavigation
import com.example.notimedi.ui.theme.NotiMediTheme
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.Activity
import android.widget.Toast
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import com.example.notimedi.data.NotiMediDatabase
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.navigation.NavHostController
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import com.example.notimedi.data.model.preferences.UserPreferences
import retrofit2.http.Part
import com.example.notimedi.repositorio.FdaRepository


class PrincipalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                BottomBarNavigation()
            }
        }
    }
}


// Pantalla Principal
@Composable
fun PrincipalScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { NotiMediDatabase.getDatabase(context) }
    val userPrefs = remember { UserPreferences(context) }
    val currentUser = userPrefs.getCurrentUser().collectAsState(initial = "").value ?: ""
    val scope = rememberCoroutineScope()

    var nombreMostrado by remember { mutableStateOf("Chayanne") }

    // Obtener nombre completo o username desde Room
    LaunchedEffect(Unit) {
        userPrefs.getCurrentUser().collect { username ->
            if (!username.isNullOrBlank()) {
                val usuario = db.usuarioDao().getUsuarioPorNombre(username)
                nombreMostrado = usuario?.nombreCompleto ?: username
            }
        }
    }

    val perfiles by db.perfilDao().obtenerPorUsuario(currentUser).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
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
                context.startActivity(Intent(context, ConfiguracionActivity::class.java))
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Configuración")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hola, $nombreMostrado.\nBienvenido a NotiMedi, desde acá puedes empezar a crear tu itinerario de medicamentos",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )

        if (perfiles.isNotEmpty()) {
            Button(
                onClick = {
                    navController.navigate("perfiles?form=true")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF))
            ) {
                Text("Agregar un nuevo perfil", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("¡Infórmate!", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
        Text("Y siempre que quieras, puedes leer estos artículos informativos.", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(16.dp))

        val articles = listOf(
            "Importancia de tomar los medicamentos a tiempo" to "https://youtu.be/sHC10PebpMo?si=YufJjWjiFw7Vnom0",
            "¿Puedo tomar alcohol con mi medicamento?" to "https://youtu.be/VQlWV1o7buQ?si=YtJo2-wnfoqeOh3a",
            "Consejos para organizar tus medicamentos" to "https://youtu.be/4RNQge_Zr-g?si=CdP_UnJxyVo_ZbU2",
            "Evita errores comunes al tomar tus medicinas" to "https://youtu.be/LCe1vORmqJQ?si=HCEYE9I_OjgnPd6-",
            "¿Cómo sabe la pastilla donde me duele?" to "https://youtu.be/qmO41t84REc?si=6mCkPvFIb9pHKw7z"
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
                    Text("Haz clic para ver más información en video", fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

// Pantalla Perfiles
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PerfilesScreen(activarFormulario: Boolean = false) {
    val context = LocalContext.current
    val db = remember { NotiMediDatabase.getDatabase(context) }
    val userPrefs = remember { UserPreferences(context) }
    val currentUser = userPrefs.getCurrentUser().collectAsState(initial = "").value ?: ""
    val perfiles by db.perfilDao().obtenerPorUsuario(currentUser).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var mostrarFormulario by remember { mutableStateOf(activarFormulario) }
    var nombre by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var sexo by remember { mutableStateOf("") }
    val opcionesSexo = listOf("Masculino", "Femenino")
    var fechaNacimiento by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            fechaNacimiento = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    var alergiaInput by remember { mutableStateOf("") }
    var listaAlergias by remember { mutableStateOf(listOf<String>()) }
    var modoEdicion by remember { mutableStateOf(false) }
    var perfilEnEdicionId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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
            Text("Perfiles de itinerarios.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        if (perfiles.isEmpty() && !mostrarFormulario) {
            Text("Todavía no hay perfiles", color = Color(0xFF1B4D7A), fontWeight = FontWeight.Bold)
            Text(
                "Crear un perfil es útil si vas a usar NotiMedi por primera vez...",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = { mostrarFormulario = true },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear un perfil", color = Color.White)
            }
        } else if (mostrarFormulario) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre o apodo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = sexo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sexo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    opcionesSexo.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { sexo = it; expanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de nacimiento") },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendario", modifier = Modifier.clickable { datePickerDialog.show() })
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = alergiaInput,
                    onValueChange = { alergiaInput = it },
                    label = { Text("Alergia a medicamento") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    val nueva = alergiaInput.trim().lowercase()
                    val yaExiste = listaAlergias.any { it.lowercase() == nueva }
                    if (alergiaInput.isNotBlank() && !yaExiste) {
                        listaAlergias = listaAlergias + alergiaInput.trim()
                        alergiaInput = ""
                    } else if (yaExiste) {
                        Toast.makeText(context, "Esta alergia ya fue añadida", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }

            if (listaAlergias.isNotEmpty()) {
                Text("Alergias añadidas:", fontWeight = FontWeight.SemiBold)
                listaAlergias.forEach {
                    Text("• $it", modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val hoy = Calendar.getInstance()
                    val fechaLimite = Calendar.getInstance().apply { add(Calendar.YEAR, -99) }
                    val partes = fechaNacimiento.split("/")

                    if (nombre.isBlank()) {
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show(); return@Button
                    }
                    if (fechaNacimiento.isBlank() || partes.size != 3) {
                        Toast.makeText(context, "Seleccioná una fecha válida", Toast.LENGTH_SHORT).show(); return@Button
                    }

                    val seleccionada = Calendar.getInstance().apply {
                        set(partes[2].toInt(), partes[1].toInt() - 1, partes[0].toInt())
                    }
                    if (seleccionada.after(hoy)) {
                        Toast.makeText(context, "La fecha no puede ser futura", Toast.LENGTH_SHORT).show(); return@Button
                    }
                    if (seleccionada.before(fechaLimite)) {
                        Toast.makeText(context, "La fecha no puede ser mayor a 99 años atrás", Toast.LENGTH_SHORT).show(); return@Button
                    }

                    val alergiasSinDuplicados = listaAlergias.map { it.lowercase() }.toSet()
                    if (alergiasSinDuplicados.size < listaAlergias.size) {
                        Toast.makeText(context, "Hay alergias repetidas", Toast.LENGTH_SHORT).show(); return@Button
                    }

                    scope.launch {
                        val esPrincipal = if (modoEdicion) {
                            perfiles.find { it.id == perfilEnEdicionId }?.esPrincipal ?: false
                        } else {
                            perfiles.isEmpty()
                        }

                        val perfil = com.example.notimedi.data.model.Perfil(
                            id = perfilEnEdicionId ?: 0,
                            nombre = nombre,
                            sexo = sexo,
                            fechaNacimiento = fechaNacimiento,
                            username = currentUser,
                            esPrincipal = esPrincipal
                        )

                        val perfilIdFinal = if (modoEdicion && perfilEnEdicionId != null) {
                            db.perfilDao().actualizar(perfil)
                            db.alergiaDao().eliminarPorPerfil(perfil.id)
                            perfil.id
                        } else {
                            db.perfilDao().insertar(perfil).toInt()
                        }

                        alergiasSinDuplicados.forEach { alergia ->
                            db.alergiaDao().insertar(
                                com.example.notimedi.data.model.Alergia(
                                    nombre = alergia,
                                    perfilId = perfilIdFinal,
                                    username = currentUser
                                )
                            )
                        }

                        mostrarFormulario = false
                        modoEdicion = false
                        perfilEnEdicionId = null
                        nombre = ""
                        sexo = ""
                        fechaNacimiento = ""
                        listaAlergias = emptyList()
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (modoEdicion) "Guardar cambios" else "Agregar perfil", color = Color.White)
            }
        } else {
            perfiles.forEach { perfil ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2A49)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val intent = Intent(context, PerfilInfoGeneralActivity::class.java).apply {
                                        putExtra("perfilId", perfil.id)
                                    }
                                    context.startActivity(intent)
                                }
                        ) {
                            Text(text = perfil.nombre, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(text = if (perfil.esPrincipal) "Perfil Principal" else "Perfil Secundario", color = Color.White, fontSize = 12.sp)
                        }

                        Row {
                            IconButton(onClick = {
                                nombre = perfil.nombre
                                sexo = perfil.sexo
                                fechaNacimiento = perfil.fechaNacimiento
                                perfilEnEdicionId = perfil.id
                                mostrarFormulario = true
                                modoEdicion = true
                                scope.launch {
                                    listaAlergias = db.alergiaDao().obtenerPorPerfil(perfil.id).map { it.nombre }
                                }
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Yellow)
                            }

                            IconButton(onClick = {
                                scope.launch {
                                    db.perfilDao().eliminar(perfil)
                                    db.alergiaDao().eliminarPorPerfil(perfil.id)
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeminiQueryScreen() {
    var userInput by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // 👈 estado de carga
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_lupa),
            contentDescription = "Ícono de búsqueda",
            modifier = Modifier
                .size(72.dp)
                .padding(top = 40.dp)
        )

        Text(
            text = "Busca un medicamento",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Nombre del medicamento") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    responseText = ""
                    val result = FdaRepository.consultarMedicamento(userInput)
                    responseText = result
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        ) {
            Text("Consultar")
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // 🔄 Mostrar indicador de carga si isLoading está en true
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF5D7FBF))
        }

        if (responseText.isNotBlank()) {
            Surface(
                color = Color(0xFFF0F0F5),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = responseText,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Advertencia",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Evita automedicarte",
                fontSize = 12.sp,
                color = Color(0xFFD32F2F)
            )
        }
    }
}
