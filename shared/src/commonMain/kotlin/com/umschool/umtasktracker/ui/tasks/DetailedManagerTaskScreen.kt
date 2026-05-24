package com.umschool.umtasktracker.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umschool.umtasktracker.ui.theme.CardBackground
import com.umschool.umtasktracker.domain.model.ManagerTask
import com.umschool.umtasktracker.presentation.manager.ManagerTasksViewModel
import com.umschool.umtasktracker.presentation.util.DateFormatter
import com.umschool.umtasktracker.ui.theme.ProgressColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DetailedTaskScreen(
    task: ManagerTask,
    viewModel: ManagerTasksViewModel,
    onBack: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(task.id) {
        viewModel.loadTaskDetails(task.id)
    }

    val curators = uiState.value.taskDetails

    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredCurators = curators.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

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
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxSize()
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    item {

                        Text(
                            text = task.title,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier.size(120.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                CircularProgressIndicator(
                                    progress = {
                                        (task.progress.toFloat() / 100f)
                                            .coerceIn(0f, 1f)
                                    },
                                    modifier = Modifier.size(120.dp),
                                    strokeWidth = 10.dp
                                )

                                Text(
                                    text = "${task.progress.toInt()}%",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(32.dp))

                            Column {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        border = ButtonDefaults.outlinedButtonBorder,
                                        color = Color.Transparent
                                    ) {
                                        Text(
                                            text = task.status.label,
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Text(
                                        text = "${task.completed}/${task.total}",
                                        color = Color(0xFF24C78B),
                                        fontSize = 20.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Создано: ${DateFormatter.formatMoscow(task.created) ?: "—"}",
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Дедлайн: ${DateFormatter.formatMoscow(task.deadline) ?: "—"}",
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Описание",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = task.description,
                            fontSize = 20.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Формат отчета",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = task.report,
                            fontSize = 20.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Исполнители",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Введите фамилию куратора")
                            },
                            trailingIcon = {
                                Row {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = ProgressColor
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    items(filteredCurators) { curator ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = curator.name,
                                        fontSize = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        border = ButtonDefaults.outlinedButtonBorder,
                                        color = Color.Transparent
                                    ) {
                                        Text(
                                            text = curator.status.label,
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {

                            OutlinedButton(
                                onClick = onBack,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Закрыть")
                            }
                        }
                    }
                }
            }
        }
    }
}