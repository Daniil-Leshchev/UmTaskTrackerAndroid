package com.umschool.umtasktracker.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.presentation.util.DateFormatter
import com.umschool.umtasktracker.ui.theme.CardBackground

@Composable
fun DetailedCuratorTaskScreen(
    task: CuratorTask,
    onBack: () -> Unit
) {

    Scaffold(
        containerColor = CardBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    item {

                        Text(
                            text = "Название задачи",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = task.title,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Описание задачи",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = task.description,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Формат отчета",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = task.reportTemplate,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Дедлайн",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = DateFormatter.formatMoscow(task.deadline) ?: "—",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        OutlinedButton(
                            onClick = onBack,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Закрыть")
                        }
                    }
                }
            }
        }
    }
}