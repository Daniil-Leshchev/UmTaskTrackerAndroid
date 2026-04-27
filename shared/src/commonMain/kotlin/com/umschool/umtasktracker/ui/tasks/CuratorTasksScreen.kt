package com.umschool.umtasktracker.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.umschool.umtasktracker.presentation.curator.CuratorTasksViewModel
import com.umschool.umtasktracker.ui.tasks.components.CuratorTaskItem
import com.umschool.umtasktracker.ui.tasks.components.TaskStatusBar
import com.umschool.umtasktracker.ui.theme.UmOrange
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.umschool.umtasktracker.ui.theme.CardBackground

@Composable
fun CuratorTasksScreen(
    viewModel: CuratorTasksViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CardBackground)
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(0.1.dp, Color.LightGray)
                .padding(horizontal = 130.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Задачи",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = uiState.tasks.size.toString(),
                    color = Color.White,
                    modifier = Modifier
                        .background(UmOrange, RoundedCornerShape(100))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(100))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TaskStatusBar(
            uiState = uiState,
            onFilterSelected = viewModel::onFilterSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = { Text("Введите название задачи") },
            trailingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Поиск",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
            cursorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            ),
        shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.filteredTasks) { task ->
                CuratorTaskItem(task = task)
            }
        }
    }
}
