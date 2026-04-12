package com.umschool.umtasktracker.ui.tasks.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskCard() {
    Card(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text("Последний тест")

            Spacer(modifier = Modifier.height(8.dp))

            Text("50%")

            Spacer(modifier = Modifier.height(8.dp))

            Text("25.03.2026")
        }
    }
}