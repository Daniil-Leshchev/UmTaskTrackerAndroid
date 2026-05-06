package com.umschool.umtasktracker.ui.tasks.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.umschool.umtasktracker.presentation.curator.CuratorTasksUiState
import com.umschool.umtasktracker.presentation.curator.TaskFilter
import com.umschool.umtasktracker.ui.theme.UmOrange
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import umtasktracker.shared.generated.resources.Res
import umtasktracker.shared.generated.resources.*

@Composable
fun TaskStatusBar(
    state: TaskStatusBarState,
    onFilterSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        StatusBlock(
            imageRes = Res.drawable.ic_completed,
            label = "Завершено",
            value = state.completedCount.toString(),
            color = Color.Green,
            onClick = { onFilterSelected(TaskFilter.COMPLETED.name) }
        )

        StatusBlock(
            imageRes = Res.drawable.ic_in_progress,
            label = "В работе",
            value = state.inProgressCount.toString(),
            color = MaterialTheme.colorScheme.primary,
            onClick = { onFilterSelected(TaskFilter.IN_PROGRESS.name) }
        )

        StatusBlock(
            imageRes = Res.drawable.ic_overdue,
            label = "Просрочено",
            value = state.overdueCount.toString(),
            color = MaterialTheme.colorScheme.error,
            onClick = { onFilterSelected(TaskFilter.OVERDUE.name) }
        )

        StatusBlock(
            imageRes = Res.drawable.Bear,
            label = "Выполнено в срок",
            value = "${state.successPercent}%",
            color = UmOrange,
            onClick = { onFilterSelected(TaskFilter.COMPLETED_ON_TIME.name) }
        )
    }
}

@Composable
private fun StatusBlock(
    imageRes: DrawableResource,
    label: String,
    value: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(82.dp)
            .height(90.dp)
            .border(0.3.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(4.dp)
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Black
        )
    }
}

data class TaskStatusBarState(
    val completedCount: Int,
    val inProgressCount: Int,
    val overdueCount: Int,
    val successPercent: Int
)