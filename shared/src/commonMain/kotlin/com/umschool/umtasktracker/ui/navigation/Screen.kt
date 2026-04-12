package com.umschool.umtasktracker.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object NotApproved : Screen("notApproved")
    data object Home : Screen("home/{roleType}") {
        fun createRoute(role: String) = "home/$role"
    }
}
