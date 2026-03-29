package com.umschool.umtasktracker.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.ui.auth.LoginScreen
import com.umschool.umtasktracker.ui.auth.RegisterScreen
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val tokenStorage: TokenStorage = koinInject()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = tokenStorage.getAccessToken().firstOrNull()
        startDestination = if (token != null) {
            Screen.Home.createRoute("manager")
        } else {
            Screen.Login.route
        }
    }

    val start = startDestination ?: return

    NavHost(navController = navController, startDestination = start) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val roleType = when (role) {
                        is UserRole.Curator -> "curator"
                        is UserRole.Manager -> "manager"
                        is UserRole.Admin -> "admin"
                    }
                    navController.navigate(Screen.Home.createRoute(roleType)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onLoginSuccess = { role ->
                    val roleType = when (role) {
                        is UserRole.Curator -> "curator"
                        is UserRole.Manager -> "manager"
                        is UserRole.Admin -> "admin"
                    }
                    navController.navigate(Screen.Home.createRoute(roleType)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegistrationSuccess = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("roleType") { type = NavType.StringType })
        ) { backStackEntry ->
            val roleType = backStackEntry.arguments?.getString("roleType") ?: "manager"
            HomeScreenStub(
                roleType = roleType,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeScreenStub(
    roleType: String,
    onLogout: () -> Unit
) {
    val tokenStorage: TokenStorage = koinInject()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Добро пожаловать!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            scope.launch {
                tokenStorage.clearTokens()
                onLogout()
            }
        }) {
            Text("Выйти")
        }
    }
}
