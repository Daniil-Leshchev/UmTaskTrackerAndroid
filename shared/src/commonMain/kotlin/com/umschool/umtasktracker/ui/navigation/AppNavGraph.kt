package com.umschool.umtasktracker.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.domain.repository.AuthRepository
import com.umschool.umtasktracker.ui.auth.LoginScreen
import com.umschool.umtasktracker.ui.auth.NotApprovedScreen
import com.umschool.umtasktracker.ui.auth.RegisterScreen
import com.umschool.umtasktracker.ui.tasks.CuratorTasksScreen
import com.umschool.umtasktracker.ui.tasks.ManagerTasksScreen
import kotlinx.coroutines.flow.firstOrNull
import org.koin.compose.koinInject

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val tokenStorage: TokenStorage = koinInject()
    val authRepository: AuthRepository = koinInject()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = tokenStorage.getAccessToken().firstOrNull()

        if (token == null) {
            startDestination = Screen.Login.route
            return@LaunchedEffect
        }

        val result = authRepository.getProfile(token)

        result
            .onSuccess { profile ->

                if (!profile.isApproved) {
                    startDestination = Screen.NotApproved.route
                    return@onSuccess
                }

                val roleType = when (UserRole.fromProfile(profile)) {
                    is UserRole.Curator -> "curator"
                    is UserRole.Manager -> "manager"
                    is UserRole.Admin -> "admin"
                }
                startDestination = Screen.Home.createRoute(roleType)
            }
            .onFailure {
                tokenStorage.clearTokens()
                startDestination = Screen.Login.route
            }
    }

    val start = startDestination
    if (start == null) return

    NavHost(navController = navController, startDestination = start) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role, isApproved ->
                    if (!isApproved) {
                        navController.navigate(Screen.NotApproved.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        return@LoginScreen
                    }
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
                onRegistrationSuccess = {
                    navController.navigate(Screen.NotApproved.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.NotApproved.route) {
            NotApprovedScreen(onGoToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            })
        }

        composable(
            route = Screen.Home.route,
            arguments = listOf(navArgument("roleType") { type = NavType.StringType })
        ) { backStackEntry ->

            val roleType = backStackEntry.arguments?.getString("roleType")

            when (roleType) {
                "manager" -> ManagerTasksScreen()
                "admin" -> ManagerTasksScreen()
                "curator" -> CuratorTasksScreen()
                else -> Text("Unknown role")
            }
        }
    }
}