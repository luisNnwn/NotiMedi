package com.example.notimedi.ui.navigation

import androidx.compose.animation.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.example.notimedi.GeminiQueryScreen
import com.example.notimedi.PerfilesScreen
import com.example.notimedi.PrincipalScreen
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.addBottomNavGraph(navController: NavHostController) {
    composable(
        route = "inicio",
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
    ) {
        PrincipalScreen(navController)
    }

    composable(
        route = "perfiles?form={form}",
        arguments = listOf(
            navArgument("form") { defaultValue = "false" }
        ),
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
    ) { backStackEntry ->
        val activarFormulario = backStackEntry.arguments?.getString("form") == "true"
        PerfilesScreen(activarFormulario = activarFormulario)
    }

    composable(
        route = "medicamentos",
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
    ) {
        GeminiQueryScreen()
    }
}
