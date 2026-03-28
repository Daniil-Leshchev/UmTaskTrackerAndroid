package com.umschool.umtasktracker.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Определяем стартовый экран по наличию токена
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = tokenStorage.getAccessToken().firstOrNull()
        startDestination = if (token != null) {
            // Если токен есть — переходим на главный экран (роль Manager по умолчанию)
            // В будущем: запросить /api/users/me/ для определения роли
            Screen.Home.createRoute("manager")
        } else {
            Screen.Login.route
        }
    }

    // Ждём определения стартового экрана
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
                    // Авто-вход после регистрации → на главный экран
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
                    // Фоллбэк: если автовход не удался → на логин
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
            // Временная заглушка — будет заменена в следующих сессиях
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

// Временная заглушка главного экрана
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
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            // Очистить токены и перейти на логин
            scope.launch {
                tokenStorage.clearTokens()
                onLogout()
            }
        }) {
            Text("Выйти")
        }
    }
}
