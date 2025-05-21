package com.example.notimedi

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notimedi.ui.theme.BottomCardShape
import com.example.notimedi.ui.theme.NotiMediTheme
import com.example.notimedi.R.font.poppins_regular
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.content.Intent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.lifecycle.lifecycleScope
import com.example.notimedi.data.model.preferences.UserPreferences
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPrefs = UserPreferences(this)

        setContent {
            val loginState by userPrefs.getLoginStatus().collectAsState(initial = null)
            val onboardingShown by userPrefs.isOnboardingShown().collectAsState(initial = false)

            NotiMediTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when {
                        loginState == null -> {
                            LoaderUI()
                        }

                        loginState == true -> {
                            LoaderUI()
                            LaunchedEffect(Unit) {
                                delay(3000)
                                val siguiente = if (tienePermisoNotificaciones()) {
                                    PrincipalActivity::class.java
                                } else {
                                    NotificacionesInfoActivity::class.java
                                }
                                startActivity(Intent(this@MainActivity, siguiente))
                                finish()
                            }
                        }

                        loginState == false && !onboardingShown -> {
                            OnboardingUI {
                                lifecycleScope.launch {
                                    userPrefs.setOnboardingShown()
                                }
                            }
                        }

                        else -> {
                            val intent = Intent(this@MainActivity, LoginRegistroActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingPager(
    item: List<OnBoardingData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(item[pagerState.currentPage].backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Image(
                        painter = painterResource(id = item[page].image),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = BottomCardShape.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item[pagerState.currentPage].mainText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp, end = 30.dp),
                            color = Color.Black,
                            fontFamily = PoppinsRegular,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = item[pagerState.currentPage].subText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 40.dp, end = 20.dp),
                            color = Color.Black,
                            fontFamily = PoppinsRegular,
                            textAlign = TextAlign.Justify,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraLight
                        )
                        PagerIndicator(
                            items = item,
                            currentPage = pagerState.currentPage
                        )
                    }

                    val coroutineScope = rememberCoroutineScope()
                    val context = LocalContext.current


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (pagerState.currentPage != 2) {
                            TextButton(onClick = {
                                context.startActivity(Intent(context, LoginRegistroActivity::class.java))
                                if (context is Activity) {
                                    context.finish()
                                }
                            }) {
                                Text(
                                    text = "Omitir",
                                    color = Color(0xFF5D7FBF),
                                    fontFamily = PoppinsRegular,
                                    textAlign = TextAlign.Right,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (pagerState.currentPage < item.lastIndex) {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                },
                                border = BorderStroke(2.dp, Color(0xFF5D7FBF)),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color(0xFF5D7FBF)
                                ),
                                modifier = Modifier.size(75.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.right_arrow),
                                    contentDescription = "",
                                    modifier = Modifier.size(35.dp),
                                    tint = Color(0xFF5D7FBF)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                   onFinished()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF5D7FBF)
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(60.dp)
                            ) {
                                Text(
                                    text = "Comenzar",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }


                }
            }
        }
    }
}

@Composable
fun PagerIndicator(items: List<OnBoardingData>, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        repeat(items.size) {
            Indicator(
                isSelected = it == currentPage,
                color = items[it].mainColor,
                inactiveColor = items[it].backgroundColor
            )
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean, color: Color, inactiveColor: Color) {
    val width = animateDpAsState(
        targetValue = if (isSelected) 40.dp else 10.dp,
        label = ""
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(if (isSelected) color else inactiveColor)
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingUI(onFinished: () -> Unit) {
    NotiMediTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val items = listOf(
                OnBoardingData(R.drawable.onboarding1, Color(0xFF2B3D66), mainColor = Color.White, mainText = "Organiza la hora de tus medicinas", subText = "Tómate un tiempo para crear tu horario"),
                OnBoardingData(R.drawable.onboarding2, Color(0xFF2B3D66), mainColor = Color.White, mainText = "Recibe notificaciones", subText = "Y no pierdas el horario de tus medicamentos"),
                OnBoardingData(R.drawable.onboarding3, Color(0xFF2B3D66), mainColor = Color.White, mainText = "¡Tu salud en tus manos!", subText = "Comienza a crear tu itinerario de medicamentos")
            )
            val pagerState = rememberPagerState(
                pageCount = items.size,
                initialOffscreenLimit = 2,
                infiniteLoop = false,
                initialPage = 0
            )
            OnBoardingPager(
                item = items,
                pagerState = pagerState,
                modifier = Modifier.fillMaxWidth(),
                onFinished = onFinished
            )
        }
    }
}



@Composable
fun LoaderUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2B3D66)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 4.dp
        )
    }
}

fun Activity.tienePermisoNotificaciones(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

