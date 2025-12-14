package com.opio.hypertensionmonitorapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome")    { WelcomeScreen(navController) }
        composable("choice")     { ChoiceScreen(navController) }
        composable("register")   { RegisterScreen(navController) }
        composable("login")      { LoginScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
    }
}