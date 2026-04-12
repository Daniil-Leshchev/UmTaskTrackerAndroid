package com.umschool.umtasktracker.ui.tasks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun TaskStatusBar() {
    Row(horizontalArrangement = Arrangement.SpaceAround) {
        TaskStatusItem("Завершено", 1)
        TaskStatusItem("В работе", 1)
        TaskStatusItem("Просрочено", 1)
        TaskStatusItem("Выполнено в срок", "33%")
    }
}
@Composable
fun TaskStatusItem(label: String, value: Any) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString())
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}