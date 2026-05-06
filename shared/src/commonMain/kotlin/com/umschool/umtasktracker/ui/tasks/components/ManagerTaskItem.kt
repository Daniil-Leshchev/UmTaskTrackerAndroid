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
import com.umschool.umtasktracker.domain.model.ManagerTask

@Composable
fun ManagerTaskItem(task: ManagerTask) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
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
}