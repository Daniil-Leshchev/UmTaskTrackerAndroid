package com.umschool.umtasktracker.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object NotApproved : Screen("notApproved")
    data object Home : Screen("home/{roleType}") {
        fun createRoute(role: String) = "home/$role"
    }
    data object CreateTask : Screen("create_task")
    data object ManagerTaskDetails :
        Screen("manager_task_details/{taskId}") {

        fun createRoute(taskId: String) =
            "manager_task_details/$taskId"
    }

    data object CuratorTaskDetails :
        Screen("curator_task_details/{taskId}") {

        fun createRoute(taskId: String) =
            "curator_task_details/$taskId"
    }
}
