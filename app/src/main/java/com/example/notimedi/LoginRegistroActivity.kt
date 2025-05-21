package com.example.notimedi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.ui.theme.NotiMediTheme
import com.example.notimedi.R.font.poppins_regular
import com.example.notimedi.data.NotiMediDatabase
import com.example.notimedi.data.model.Usuario
import com.example.notimedi.data.model.dao.UsuarioDao
import com.example.notimedi.data.model.preferences.UserPreferences
import kotlinx.coroutines.launch
import com.example.notimedi.rememberFirebaseAuthLauncher
import android.Manifest
import android.app.Activity
import com.example.notimedi.tienePermisoNotificaciones
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.core.content.ContextCompat
import com.example.notimedi.util.desencriptarPin

class LoginRegistroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginRegistroScreen()
                }
            }
        }
    }
}

@Composable
fun LoginRegistroScreen() {
    var isLoginSelected by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val db = remember { NotiMediDatabase.getDatabase(context) }
    val usuarioDao = remember { db.usuarioDao() }
    val userPrefs = remember { UserPreferences(context) }

    LaunchedEffect(Unit) {
        userPrefs.getLoginStatus().collect { isLogged ->
            if (isLogged) {
                val intent = Intent(context, NotificacionesInfoActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.notimedilogo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "NOTI MEDI",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = "Tu compañero confiable para tomar tus medicamentos",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        val switchWidth = 320.dp
        val switchHeight = 56.dp
        val animatedOffset by animateDpAsState(
            targetValue = if (isLoginSelected) 0.dp else switchWidth / 2,
            animationSpec = tween(durationMillis = 300),
            label = "switchAnimation"
        )

        Box(
            modifier = Modifier
                .width(switchWidth)
                .height(switchHeight)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF555555))
        ) {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(switchWidth / 2)
                    .height(switchHeight)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF5D7FBF))
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { isLoginSelected = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        color = if (isLoginSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )


                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { isLoginSelected = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Registrarse",
                        color = if (!isLoginSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp)
                .background(color = Color(0xFF2B3D66), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(24.dp)
        ) {
            Crossfade(targetState = isLoginSelected, label = "formSwitch") { showLogin ->
                if (showLogin) LoginForm(
                    onLogin = {
                        val destino = if ((context as Activity).tienePermisoNotificaciones()) {
                            PrincipalActivity::class.java
                        } else {
                            NotificacionesInfoActivity::class.java
                        }

                        val intent = Intent(context, destino).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    },
                    usuarioDao = usuarioDao
                ) else RegistroForm(
                    onRegistered = { isLoginSelected = true },
                    usuarioDao = usuarioDao
                )
            }
        }
    }
}

@Composable
fun LoginForm(onLogin: () -> Unit, usuarioDao: UsuarioDao) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    // Estado del popup
    var showResetDialog by remember { mutableStateOf(false) }

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { user ->
            scope.launch {
                val usernameGoogle = (user.email ?: user.id ?: "google_user").trim().lowercase()
                val nombreCompleto = user.displayName ?: "Usuario Google"

                val dao = NotiMediDatabase.getDatabase(context).usuarioDao()
                val existente = dao.getUsuarioPorNombre(usernameGoogle)
                if (existente == null) {
                    dao.insertar(Usuario(username = usernameGoogle, password = "", nombreCompleto = nombreCompleto))
                }

                userPrefs.setLoginStatus(true, usernameGoogle)
                onLogin()
            }
        },
        onAuthError = {
            errorMsg = "Error al iniciar sesión con Google"
        }
    )

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text("Bienvenido/a", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color.White) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        errorMsg?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 8.dp)
                .clickable { showResetDialog = true }
        )

        Button(
            onClick = {
                scope.launch {
                    val user = usuarioDao.login(username.trim().lowercase(), password)
                    if (user != null) {
                        userPrefs.setLoginStatus(true, username.trim().lowercase())
                        onLogin()
                    } else {
                        errorMsg = "Usuario o contraseña incorrectos"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF))
        ) {
            Text(text = "Iniciar Sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { launcher.launch() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Sign-In",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Iniciar sesión con Google", color = Color.White)
            }
        }
    }
    // POPUP DE RECUPERACIÓN
    if (showResetDialog) {
        var resetUsername by remember { mutableStateOf("") }
        var resetPin by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmNewPassword by remember { mutableStateOf("") }
        var resetError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        if (resetUsername.isBlank() || resetPin.isBlank() ||
                            newPassword.isBlank() || confirmNewPassword.isBlank()) {
                            resetError = "Completa todos los campos"
                            return@launch
                        }

                        val cleanUsername = resetUsername.trim().lowercase()
                        val usuario = usuarioDao.getUsuarioPorNombre(cleanUsername)
                        if (usuario == null) {
                            resetError = "El usuario no existe"
                            return@launch
                        }

                        if (usuario.pin.isNullOrBlank()) {
                            resetError = "El usuario no tiene PIN de seguridad"
                            return@launch
                        }

                        val pinDesencriptado = desencriptarPin(usuario.pin!!)
                        if (resetPin != pinDesencriptado) {
                            resetError = "El PIN no es correcto"
                            return@launch
                        }

                        if (newPassword != confirmNewPassword) {
                            resetError = "Las contraseñas no coinciden"
                            return@launch
                        }

                        usuarioDao.actualizarPassword(resetUsername, newPassword)
                        resetError = null
                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()

                        // ✅ Cerrar popup y limpiar campos
                        resetUsername = ""
                        resetPin = ""
                        newPassword = ""
                        confirmNewPassword = ""
                        showResetDialog = false
                    }
                }) {
                    Text("Actualizar")
                }
            }
            ,
            title = { Text("Recuperar Contraseña") },
            text = {
                Column {
                    OutlinedTextField(
                        value = resetUsername,
                        onValueChange = { resetUsername = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = resetPin,
                        onValueChange = { resetPin = it.filter { c -> c.isDigit() }.take(6) },
                        label = { Text("PIN de seguridad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Repite la nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    resetError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = it, color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun RegistroForm(onRegistered: () -> Unit, usuarioDao: UsuarioDao) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color.White) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Reescribe tu contraseña", color = Color.White) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        errorMsg?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    val cleanUsername = username.trim().lowercase()
                    if (cleanUsername.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        errorMsg = "Todos los campos son obligatorios"
                        return@launch
                    }
                    if (password != confirmPassword) {
                        errorMsg = "Las contraseñas no coinciden"
                        return@launch
                    }
                    val existingUser = usuarioDao.getUsuarioPorNombre(cleanUsername)
                    if (existingUser != null) {
                        errorMsg = "Ese usuario ya existe"
                    } else {
                        usuarioDao.insertar(Usuario(cleanUsername, password))
                        onRegistered()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF))
        ) {
            Text(text = "Registrarse", color = Color.White)
        }
    }
}
