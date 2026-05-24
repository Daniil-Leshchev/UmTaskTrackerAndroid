package com.umschool.umtasktracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.umschool.umtasktracker.notifications.UmFirebaseMessagingService
import com.umschool.umtasktracker.ui.navigation.AppNavGraph
import com.umschool.umtasktracker.ui.theme.UmTaskTrackerTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private val pendingTaskId = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            val taskId by pendingTaskId.collectAsState()
            UmTaskTrackerTheme {
                AppNavGraph(
                    pendingTaskId = taskId,
                    onDeepLinkConsumed = { pendingTaskId.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val taskId = intent?.getStringExtra(UmFirebaseMessagingService.EXTRA_TASK_ID) ?: return
        pendingTaskId.value = taskId
    }
}
