package com.umschool.umtasktracker.ui.tasks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umschool.umtasktracker.domain.model.CuratorTask

@Composable
fun CuratorTaskItem(task: CuratorTask) {
    Card(
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = task.deadline, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
//                when (task.status) {
//                    TaskStatus.COMPLETED -> Icon(Icons.Filled.CheckCircle, null, tint = Color.Green)
//                    TaskStatus.IN_PROGRESS -> Icon(Icons.Filled.Schedule, null, tint = Color.Blue)
//                    TaskStatus.OVERDUE -> Icon(Icons.Filled.Cancel, null, tint = Color.Red)
//                }
//                Spacer(modifier = Modifier.width(8.dp))
                Text(text = task.status.label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
