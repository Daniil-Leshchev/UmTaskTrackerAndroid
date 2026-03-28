package com.umschool.umtasktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.umschool.umtasktracker.ui.navigation.AppNavGraph
import com.umschool.umtasktracker.ui.theme.UmTaskTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UmTaskTrackerTheme {
                AppNavGraph()
            }
        }
    }
}
