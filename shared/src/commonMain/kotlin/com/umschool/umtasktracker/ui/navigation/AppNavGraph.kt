package com.umschool.umtasktracker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import com.umschool.umtasktracker.ui.theme.UmOrange
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.umschool.umtasktracker.data.local.TokenStorage
import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.domain.repository.AuthRepository
import com.umschool.umtasktracker.notifications.FcmTokenRegistrar
import com.umschool.umtasktracker.presentation.manager.ManagerTasksViewModel
import com.umschool.umtasktracker.ui.auth.LoginScreen
import com.umschool.umtasktracker.ui.auth.NotApprovedScreen
import com.umschool.umtasktracker.ui.auth.RegisterScreen
import com.umschool.umtasktracker.ui.tasks.CreateTaskAssignmentScreen
import com.umschool.umtasktracker.ui.tasks.CreateTaskScreen
import com.umschool.umtasktracker.ui.tasks.CuratorTasksScreen
import com.umschool.umtasktracker.ui.tasks.ManagerTasksScreen
import kotlinx.coroutines.flow.firstOrNull
import org.koin.compose.koinInject
import com.umschool.umtasktracker.ui.tasks.DetailedTaskScreen
import org.koin.compose.viewmodel.koinViewModel
import com.umschool.umtasktracker.presentation.curator.CuratorTasksViewModel
import com.umschool.umtasktracker.ui.tasks.DetailedCuratorTaskScreen

@Composable
fun AppNavGraph(
    pendingTaskId: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val navController = rememberNavController()
    val tokenStorage: TokenStorage = koinInject()
    val authRepository: AuthRepository = koinInject()
    val fcmTokenRegistrar: FcmTokenRegistrar = koinInject()

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
                fcmTokenRegistrar.unregisterOnLogout()
                tokenStorage.clearTokens()
                startDestination = Screen.Login.route
            }
    }

    val start = startDestination
    if (start == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = UmOrange)
        }
        return
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(pendingTaskId, currentRoute) {
        val taskId = pendingTaskId ?: return@LaunchedEffect
        if (currentRoute == null) return@LaunchedEffect
        val role = currentBackStackEntry?.arguments?.getString("roleType")
        if (currentRoute == Screen.Home.route && role == "curator") {
            navController.navigate(Screen.CuratorTaskDetails.createRoute(taskId))
        }
        onDeepLinkConsumed()
    }

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
                "manager" -> ManagerTasksScreen(
                    onCreateTask = { navController.navigate(Screen.CreateTaskFlow.route) },
                    onTaskClick = { taskId ->
                        navController.navigate(Screen.ManagerTaskDetails.createRoute(taskId))
                    }
                )

                "admin" -> ManagerTasksScreen(
                    onCreateTask = { navController.navigate(Screen.CreateTaskFlow.route) },
                    onTaskClick = { taskId ->
                        navController.navigate(Screen.ManagerTaskDetails.createRoute(taskId))
                    }
                )
                "curator" -> CuratorTasksScreen(
                    onTaskClick = { taskId ->
                        navController.navigate(Screen.CuratorTaskDetails.createRoute(taskId))
                    }
                )
                else -> Text("Unknown role")
            }
        }

        navigation(
            startDestination = Screen.CreateTaskMain.route,
            route = Screen.CreateTaskFlow.route
        ) {
            composable(Screen.CreateTaskMain.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.CreateTaskFlow.route)
                }
                CreateTaskScreen(
                    viewModelStoreOwner = parentEntry,
                    onCancel = {
                        navController.popBackStack(Screen.CreateTaskFlow.route, inclusive = true)
                    },
                    onNavigateToAssignment = {
                        navController.navigate(Screen.CreateTaskAssignment.route)
                    }
                )
            }
            composable(Screen.CreateTaskAssignment.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.CreateTaskFlow.route)
                }
                CreateTaskAssignmentScreen(
                    viewModelStoreOwner = parentEntry,
                    onBack = { navController.popBackStack() },
                    onTaskCreated = {
                        navController.popBackStack(Screen.CreateTaskFlow.route, inclusive = true)
                    }
                )
            }
        }

        composable(
            route = Screen.ManagerTaskDetails.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val taskId =
                backStackEntry.arguments?.getString("taskId")
                    ?: return@composable

            val homeEntry = try {
                navController.getBackStackEntry(
                    Screen.Home.createRoute("manager")
                )
            } catch (_: Exception) {
                navController.getBackStackEntry(
                    Screen.Home.createRoute("admin")
                )
            }

            val viewModel: ManagerTasksViewModel =
                koinViewModel(viewModelStoreOwner = homeEntry)

            val task = viewModel.getTaskById(taskId)

            if (task != null) {

                DetailedTaskScreen(
                    task = task,
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = Screen.CuratorTaskDetails.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val taskId =
                backStackEntry.arguments?.getString("taskId")
                    ?: return@composable

            val homeEntry = navController.getBackStackEntry(
                Screen.Home.createRoute("curator")
            )

            val viewModel: CuratorTasksViewModel =
                koinViewModel(viewModelStoreOwner = homeEntry)

            val uiState by viewModel.uiState.collectAsState()
            val task = viewModel.getTaskById(taskId)

            LaunchedEffect(taskId) {
                if (viewModel.getTaskById(taskId) == null) {
                    viewModel.loadTasks()
                }
            }

            when {
                task != null -> DetailedCuratorTaskScreen(
                    task = task,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = UmOrange)
                }
                else -> Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Задача не найдена")
                        Spacer(Modifier.height(12.dp))
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Назад", color = UmOrange)
                        }
                    }
                }
            }
        }
    }
}