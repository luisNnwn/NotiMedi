package com.example.notimedi.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomBarNavigation() {
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF2B3D66)) {
                NavigationBarItem(
                    selected = currentRoute == "inicio",
                    onClick = { navController.navigate("inicio") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = currentRoute == "perfiles",
                    onClick = { navController.navigate("perfiles") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfiles") },
                    label = { Text("Perfiles") }
                )
                NavigationBarItem(
                    selected = currentRoute == "medicamentos",
                    onClick = { navController.navigate("medicamentos") },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Medicamentos") },
                    label = { Text("Medicamentos") }
                )
            }
        }
    ) { innerPadding ->
        AnimatedNavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier.padding(innerPadding)
        ) {
            addBottomNavGraph(navController)
        }
    }
}