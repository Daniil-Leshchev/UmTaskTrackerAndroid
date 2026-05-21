package com.umschool.umtasktracker.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object NotApproved : Screen("notApproved")
    data object Home : Screen("home/{roleType}") {
        fun createRoute(role: String) = "home/$role"
    }
    data object CreateTaskFlow : Screen("create_task_flow")
    data object CreateTaskMain : Screen("create_task_main")
    data object CreateTaskAssignment : Screen("create_task_assignment")
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
