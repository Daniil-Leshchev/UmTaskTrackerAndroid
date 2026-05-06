package com.umschool.umtasktracker.ui.tasks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.TaskStatus

@Composable
fun CuratorTaskItem(task: CuratorTask) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(vertical = 2.dp),
        border = BorderStroke(0.3.dp, Color.LightGray)
    )
        {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(task.status.toColor().copy(alpha = 0.7f))) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Row {
                            Text(
                                text = task.status.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = task.status.toColor().copy(alpha = 0.7f)
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = task.deadline,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(IntrinsicSize.Max)
                        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                        .background(task.status.toColor())
                )
            }
        }
}

fun TaskStatus.toColor(): Color = when (this) {
    TaskStatus.COMPLETED -> Color.Green
    TaskStatus.IN_PROGRESS -> Color.Blue
    TaskStatus.OVERDUE -> Color.Red
    TaskStatus.COMPLETED_LATE -> Color.Green
}
