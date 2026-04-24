package com.umschool.umtasktracker.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.umschool.umtasktracker.presentation.curator.CuratorTasksViewModel
import com.umschool.umtasktracker.ui.tasks.components.CuratorTaskItem
import com.umschool.umtasktracker.ui.tasks.components.TaskStatusBar
import com.umschool.umtasktracker.ui.theme.ErrorRed
import com.umschool.umtasktracker.ui.theme.GradientTop
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CuratorTasksScreen(
    viewModel: CuratorTasksViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Задачи",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TaskStatusBar()

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.tasks) { task ->
                CuratorTaskItem(task = task)
            }
        }
    }
}
