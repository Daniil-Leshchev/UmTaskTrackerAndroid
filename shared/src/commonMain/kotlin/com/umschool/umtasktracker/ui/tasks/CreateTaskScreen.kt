package com.umschool.umtasktracker.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.umschool.umtasktracker.domain.model.Recipient
import com.umschool.umtasktracker.presentation.manager.CreateTaskViewModel
import com.umschool.umtasktracker.presentation.util.DateFormatter
import com.umschool.umtasktracker.ui.theme.CardBackground
import com.umschool.umtasktracker.ui.theme.ErrorRed
import com.umschool.umtasktracker.ui.theme.TextDark
import com.umschool.umtasktracker.ui.theme.TextHint
import com.umschool.umtasktracker.ui.theme.UmOrange
import com.umschool.umtasktracker.ui.theme.avatarColor
import org.koin.compose.viewmodel.koinViewModel

private val SuccessGreen = Color(0xFF22C55E)
private val FieldBorder = Color(0xFFE2E5EA)

@Composable
fun CreateTaskScreen(
    onTaskCreated: () -> Unit,
    onCancel: () -> Unit,
    viewModel: CreateTaskViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess != null) onTaskCreated()
    }

    Scaffold(containerColor = CardBackground) { padding ->
        when {
            uiState.isLoadingInit -> LoadingState(padding)
            uiState.error != null && uiState.policy == null ->
                ErrorState(padding, uiState.error ?: "")
            else -> CreateTaskContent(
                padding = padding,
                taskName = uiState.taskName,
                description = uiState.description,
                deadlineDateMillis = uiState.deadlineDateMillis,
                deadlineHour = uiState.deadlineHour,
                deadlineMinute = uiState.deadlineMinute,
                reportFormat = uiState.reportFormat,
                searchQuery = uiState.searchQuery,
                filteredRecipients = uiState.filteredRecipients,
                selectedRecipients = uiState.selectedRecipients,
                isSubmitting = uiState.isSubmitting,
                validationError = uiState.validationError,
                submitError = uiState.error,
                onTaskNameChange = viewModel::onTaskNameChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onDeadlineDateChange = viewModel::onDeadlineDateChange,
                onDeadlineTimeChange = viewModel::onDeadlineTimeChange,
                onReportFormatChange = viewModel::onReportFormatChange,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onToggleRecipient = viewModel::toggleRecipient,
                onRemoveRecipient = viewModel::removeRecipient,
                onSubmit = viewModel::submitTask,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun LoadingState(padding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = UmOrange)
    }
}

@Composable
private fun ErrorState(padding: PaddingValues, message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = ErrorRed)
    }
}

@Composable
private fun CreateTaskContent(
    padding: PaddingValues,
    taskName: String,
    description: String,
    deadlineDateMillis: Long?,
    deadlineHour: Int?,
    deadlineMinute: Int?,
    reportFormat: String,
    searchQuery: String,
    filteredRecipients: List<Recipient>,
    selectedRecipients: List<Recipient>,
    isSubmitting: Boolean,
    validationError: String?,
    submitError: String?,
    onTaskNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDeadlineDateChange: (Long?) -> Unit,
    onDeadlineTimeChange: (Int, Int) -> Unit,
    onReportFormatChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleRecipient: (Recipient) -> Unit,
    onRemoveRecipient: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        ScreenHeader()

        Spacer(Modifier.height(14.dp))

        LabeledField(label = "Название задачи") {
            AppTextField(
                value = taskName,
                onValueChange = onTaskNameChange,
                placeholder = "Введите название задачи"
            )
        }

        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Описание задачи") {
            AppTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = "Опишите детали задачи",
                singleLine = false,
                minLines = 4
            )
        }

        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Дедлайн (МСК)") {
            DeadlinePickerField(
                deadlineDateMillis = deadlineDateMillis,
                deadlineHour = deadlineHour,
                deadlineMinute = deadlineMinute,
                onDateChange = onDeadlineDateChange,
                onTimeChange = onDeadlineTimeChange
            )
        }

        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Форма отчета") {
            AppTextField(
                value = reportFormat,
                onValueChange = onReportFormatChange,
                placeholder = "Введите требуемый формат отчета"
            )
        }

        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Исполнитель") {
            RecipientSearchSection(
                query = searchQuery,
                filteredRecipients = filteredRecipients,
                selectedRecipients = selectedRecipients,
                onQueryChange = onSearchQueryChange,
                onToggle = onToggleRecipient,
                onRemove = onRemoveRecipient
            )
        }

        if (validationError != null) {
            Spacer(Modifier.height(10.dp))
            Text(text = validationError, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
        }
        if (submitError != null) {
            Spacer(Modifier.height(10.dp))
            Text(text = submitError, color = ErrorRed, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.width(110.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !isSubmitting
            ) {
                Text("Отмена", color = TextDark)
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = onSubmit,
                modifier = Modifier.width(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    contentColor = Color.White
                ),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Создать")
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ScreenHeader() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Создание новой задачи",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
        )
    }
}

@Composable
private fun LabeledField(label: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextDark
        )
        Spacer(Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextHint) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(8.dp),
        colors = fieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeadlinePickerField(
    deadlineDateMillis: Long?,
    deadlineHour: Int?,
    deadlineMinute: Int?,
    onDateChange: (Long?) -> Unit,
    onTimeChange: (Int, Int) -> Unit
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val displayText =
        if (deadlineDateMillis != null && deadlineHour != null && deadlineMinute != null) {
            DateFormatter.moscowDisplayFromComponents(deadlineDateMillis, deadlineHour, deadlineMinute)
        } else ""

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDateDialog = true }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            enabled = false,
            placeholder = { Text("дд.мм.гггг, чч:мм", color = TextHint) },
            trailingIcon = {
                FieldActionButton(icon = Icons.Default.DateRange, onClick = { showDateDialog = true })
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = TextDark,
                disabledBorderColor = FieldBorder,
                disabledContainerColor = Color.White,
                disabledPlaceholderColor = TextHint,
                disabledTrailingIconColor = UmOrange
            )
        )
    }

    if (showDateDialog) {
        val today = remember { kotlinx.datetime.Clock.System.now().toEpochMilliseconds() }
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = deadlineDateMillis ?: today,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= today - 24 * 60 * 60 * 1000
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateChange(pickerState.selectedDateMillis)
                    showDateDialog = false
                    showTimeDialog = true
                }) { Text("Далее", color = UmOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog = false }) { Text("Отмена", color = TextDark) }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showTimeDialog) {
        val timeState = rememberTimePickerState(
            initialHour = deadlineHour ?: 18,
            initialMinute = deadlineMinute ?: 0,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(timeState.hour, timeState.minute)
                    showTimeDialog = false
                }) { Text("OK", color = UmOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog = false }) { Text("Отмена", color = TextDark) }
            },
            title = { Text("Время дедлайна (МСК)") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timeState)
                }
            }
        )
    }
}

@Composable
private fun FieldActionButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 6.dp)
            .size(32.dp)
            .background(UmOrange, RoundedCornerShape(6.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RecipientSearchSection(
    query: String,
    filteredRecipients: List<Recipient>,
    selectedRecipients: List<Recipient>,
    onQueryChange: (String) -> Unit,
    onToggle: (Recipient) -> Unit,
    onRemove: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Введите фамилию куратора", color = TextHint) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                FieldActionButton(icon = Icons.Default.Search, onClick = {})
            },
            colors = fieldColors()
        )

        if (selectedRecipients.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                selectedRecipients.forEach { r ->
                    SelectedRecipientChip(recipient = r, onRemove = { onRemove(r.email) })
                }
            }
        }

        if (query.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (filteredRecipients.isEmpty()) {
                    Text(
                        text = "Никого не найдено",
                        color = TextHint,
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                        items(filteredRecipients) { r ->
                            val isSelected = selectedRecipients.any { it.email == r.email }
                            RecipientSearchResultItem(
                                recipient = r,
                                isSelected = isSelected,
                                onClick = { onToggle(r) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedRecipientChip(recipient: Recipient, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .background(avatarColor(recipient.email), RoundedCornerShape(50))
            .clickable { onRemove() }
            .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shortName(recipient.name),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Убрать",
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(14.dp)
        )
    }
}

private fun shortName(fullName: String): String {
    val parts = fullName.trim().split(' ').filter { it.isNotBlank() }
    return when (parts.size) {
        0 -> ""
        1 -> parts[0]
        else -> "${parts[0]} ${parts[1]}"
    }
}

@Composable
private fun RecipientSearchResultItem(
    recipient: Recipient,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(if (isSelected) UmOrange.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shortName(recipient.name),
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Выбран",
                tint = UmOrange,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = UmOrange.copy(alpha = 0.6f),
    unfocusedBorderColor = FieldBorder,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = UmOrange
)
