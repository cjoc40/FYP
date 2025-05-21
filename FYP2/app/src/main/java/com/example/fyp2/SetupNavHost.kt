package com.example.fyp2

import Connect4ViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fyp2.pages.Account
import com.example.fyp2.pages.Connect4
import com.example.fyp2.pages.Connect4Playstyle
import com.example.fyp2.pages.Gamemode
import com.example.fyp2.pages.GridScreen
import com.example.fyp2.pages.Login2
import com.example.fyp2.pages.MctsStats
import com.example.fyp2.pages.StatCompare
import com.example.fyp2.pages.Stats
import com.example.fyp2.pages.StatsStyle
import com.example.fyp2.pages.TicPlaystyle

@Composable
fun SetupNavHost(navController: NavHostController) {
    val viewModel: TicTacToeViewModel = viewModel()
    val viewModelC: Connect4ViewModel = viewModel()

    NavHost(
        navController = navController,
//        startDestination = "Login2"
        startDestination = "Gamemode"
    ) {
        composable("login") {
            Login(navController = navController)
        }
        composable("Login2") {
            val viewModel2 = LoginViewModel()
            Login2(navController, viewModel2, LocalContext.current)
        }
        composable("Gamemode") {
            Gamemode(navController)
        }
        composable("TicPlaystyle") {
            TicPlaystyle(navController)
        }
        composable(
            route = "GridScreen/{playstyle}",
            arguments = listOf(navArgument("playstyle") { type = NavType.StringType })
        ) { backStackEntry ->
            val playstyle = backStackEntry.arguments?.getString("playstyle") ?: "PlayerVsPlayer"
            GridScreen(viewModel, navController, playstyle)
        }
        composable(
            route = "Connect4/{playstyle}",
            arguments = listOf(navArgument("playstyle") { type = NavType.StringType })
        ) { backStackEntry ->
            val playstyle = backStackEntry.arguments?.getString("playstyle") ?: "PlayerVsPlayer"
            Connect4(viewModelC, navController, playstyle)
        }
        composable("Connect4Playstyle") {
            Connect4Playstyle(navController)
        }
        composable("Account") {
            Account(navController)
        }
        composable("MctsStats") { backStackEntry ->
            MctsStats(navController, viewModelC)
        }
        composable("Stats") { backStackEntry ->
            Stats(navController, viewModel)
        }
        composable("StatsStyle") {
            StatsStyle(navController)
        }
        composable("StatCompare") {
            StatCompare(navController, viewModel, viewModelC)
        }
    }
}

