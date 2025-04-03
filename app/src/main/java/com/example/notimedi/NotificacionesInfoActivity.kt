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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.ui.theme.NotiMediTheme
import com.example.notimedi.R.font.poppins_regular

class NotificacionesInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotificacionesInfoScreen()
                }
            }
        }
    }
}

@Composable
fun NotificacionesInfoScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.avisonotificaciones),
            contentDescription = "Ilustración de notificaciones",
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(320.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Permite que puedas recibir notificaciones",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Las notificaciones automáticas te brindarán los recordatorios de los medicamentos que debes de tomar",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val intent = Intent(context, PrincipalActivity::class.java)
                context.startActivity(intent)
                (context as? android.app.Activity)?.finish()
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D7FBF))
        ) {
            Text("Continuar", color = Color.White)
        }
    }
}
