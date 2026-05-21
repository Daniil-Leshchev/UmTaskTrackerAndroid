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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import com.umschool.umtasktracker.presentation.manager.CreateTaskUiState
import com.umschool.umtasktracker.presentation.manager.CreateTaskViewModel
import com.umschool.umtasktracker.presentation.util.DateFormatter
import com.umschool.umtasktracker.ui.theme.CardBackground
import com.umschool.umtasktracker.ui.theme.ErrorRed
import com.umschool.umtasktracker.ui.theme.TextDark
import com.umschool.umtasktracker.ui.theme.TextHint
import com.umschool.umtasktracker.ui.theme.UmOrange
import org.koin.compose.viewmodel.koinViewModel

private val SuccessGreen = Color(0xFF22C55E)
private val FieldBorder = Color(0xFFE2E5EA)

@Composable
fun CreateTaskScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    onCancel: () -> Unit,
    onNavigateToAssignment: () -> Unit
) {
    val viewModel: CreateTaskViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(containerColor = CardBackground) { padding ->
        when {
            uiState.isLoadingInit -> LoadingState(padding)
            uiState.error != null && uiState.policy == null ->
                ErrorState(padding, uiState.error ?: "")
            else -> StepContent(
                padding = padding,
                uiState = uiState,
                onTaskNameChange = viewModel::onTaskNameChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onDeadlineDateChange = viewModel::onDeadlineDateChange,
                onDeadlineTimeChange = viewModel::onDeadlineTimeChange,
                onReportFormatChange = viewModel::onReportFormatChange,
                onCancel = onCancel,
                onNext = {
                    if (viewModel.validateStep1()) onNavigateToAssignment()
                }
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
private fun StepContent(
    padding: PaddingValues,
    uiState: CreateTaskUiState,
    onTaskNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDeadlineDateChange: (Long?) -> Unit,
    onDeadlineTimeChange: (Int, Int) -> Unit,
    onReportFormatChange: (String) -> Unit,
    onCancel: () -> Unit,
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

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
                value = uiState.taskName,
                onValueChange = onTaskNameChange,
                placeholder = "Введите название задачи"
            )
        }

        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Описание задачи") {
            AppTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                placeholder = "Опишите детали задачи",
                singleLine = false,
                minLines = 4
            )
        }

        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Дедлайн (МСК)") {
            DeadlinePickerField(
                deadlineDateMillis = uiState.deadlineDateMillis,
                deadlineHour = uiState.deadlineHour,
                deadlineMinute = uiState.deadlineMinute,
                onDateChange = onDeadlineDateChange,
                onTimeChange = onDeadlineTimeChange
            )
        }

        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Форма отчета") {
            AppTextField(
                value = uiState.reportFormat,
                onValueChange = onReportFormatChange,
                placeholder = "Введите требуемый формат отчета"
            )
        }

        if (uiState.validationError != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = uiState.validationError,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.width(110.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Отмена", color = TextDark)
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.width(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    contentColor = Color.White
                )
            ) {
                Text("Далее")
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
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = UmOrange.copy(alpha = 0.6f),
    unfocusedBorderColor = FieldBorder,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = UmOrange
)
