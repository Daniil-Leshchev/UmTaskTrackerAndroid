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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import com.umschool.umtasktracker.domain.model.CatalogItem
import com.umschool.umtasktracker.domain.model.Recipient
import com.umschool.umtasktracker.presentation.manager.CreateTaskViewModel
import com.umschool.umtasktracker.ui.theme.CardBackground
import com.umschool.umtasktracker.ui.theme.ErrorRed
import com.umschool.umtasktracker.ui.theme.TextDark
import com.umschool.umtasktracker.ui.theme.TextHint
import com.umschool.umtasktracker.ui.theme.UmOrange
import com.umschool.umtasktracker.ui.theme.avatarColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateTaskAssignmentScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    onBack: () -> Unit,
    onTaskCreated: () -> Unit
) {
    val viewModel: CreateTaskViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess != null) onTaskCreated()
    }

    Scaffold(containerColor = CardBackground) { padding ->
        AssignmentContent(
            padding = padding,
            canChooseDepartment = uiState.canChooseDepartment,
            availableDepartments = uiState.availableDepartments,
            availableRoles = uiState.availableRoles,
            selectedDepartmentIds = uiState.selectedDepartmentIds,
            selectedRoleIds = uiState.selectedRoleIds,
            specificCuratorMode = uiState.specificCuratorMode,
            searchQuery = uiState.searchQuery,
            filteredRecipients = uiState.filteredRecipients,
            selectedRecipients = uiState.selectedRecipients,
            isSubmitting = uiState.isSubmitting,
            validationError = uiState.validationError,
            submitError = uiState.error,
            onToggleDepartment = viewModel::toggleDepartment,
            onToggleRole = viewModel::toggleRole,
            onToggleSpecificCurator = viewModel::toggleSpecificCuratorMode,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onToggleRecipient = viewModel::toggleRecipient,
            onRemoveRecipient = viewModel::removeRecipient,
            onSubmit = viewModel::submitTask,
            onBack = onBack
        )
    }
}

@Composable
private fun AssignmentContent(
    padding: PaddingValues,
    canChooseDepartment: Boolean,
    availableDepartments: List<CatalogItem>,
    availableRoles: List<CatalogItem>,
    selectedDepartmentIds: Set<Int>,
    selectedRoleIds: Set<Int>,
    specificCuratorMode: Boolean,
    searchQuery: String,
    filteredRecipients: List<Recipient>,
    selectedRecipients: List<Recipient>,
    isSubmitting: Boolean,
    validationError: String?,
    submitError: String?,
    onToggleDepartment: (Int) -> Unit,
    onToggleRole: (Int) -> Unit,
    onToggleSpecificCurator: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleRecipient: (Recipient) -> Unit,
    onRemoveRecipient: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(searchQuery, specificCuratorMode) {
        if (searchQuery.isNotBlank() || specificCuratorMode) {
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

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Назначение задачи",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }

        Spacer(Modifier.height(16.dp))

        if (canChooseDepartment && availableDepartments.isNotEmpty()) {
            Text(
                text = "Направление:",
                style = MaterialTheme.typography.labelMedium,
                color = TextDark
            )
            Spacer(Modifier.height(6.dp))
            availableDepartments.forEach { dept ->
                AssignmentCheckboxRow(
                    checked = dept.id in selectedDepartmentIds,
                    label = dept.name,
                    onToggle = { onToggleDepartment(dept.id) }
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        if (availableRoles.isNotEmpty()) {
            Text(
                text = "Группа кураторов:",
                style = MaterialTheme.typography.labelMedium,
                color = TextDark
            )
            Spacer(Modifier.height(6.dp))
            availableRoles.forEach { role ->
                AssignmentCheckboxRow(
                    checked = role.id in selectedRoleIds,
                    label = role.name,
                    onToggle = { onToggleRole(role.id) },
                    enabled = !specificCuratorMode
                )
            }
        }

        AssignmentCheckboxRow(
            checked = specificCuratorMode,
            label = "Конкретный куратор",
            onToggle = { onToggleSpecificCurator() }
        )

        if (specificCuratorMode) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Поиск куратора(ов)",
                style = MaterialTheme.typography.labelMedium,
                color = TextDark
            )
            Spacer(Modifier.height(6.dp))
            AssignmentRecipientSearch(
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

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.width(110.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !isSubmitting
            ) {
                Text("Назад", color = TextDark)
            }
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = onSubmit,
                modifier = Modifier.width(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E),
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
private fun AssignmentCheckboxRow(
    checked: Boolean,
    label: String,
    onToggle: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onToggle() }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = UmOrange,
                uncheckedColor = TextHint,
                checkmarkColor = Color.White
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) TextDark else TextHint
        )
    }
}

@Composable
private fun AssignmentRecipientSearch(
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
                AssignmentSearchButton(icon = Icons.Default.Search)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = UmOrange.copy(alpha = 0.6f),
                unfocusedBorderColor = Color(0xFFE2E5EA),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = UmOrange
            )
        )

        if (selectedRecipients.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                selectedRecipients.forEach { r ->
                    SelectedChip(recipient = r, onRemove = { onRemove(r.email) })
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
                            SearchResultRow(
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
private fun AssignmentSearchButton(icon: ImageVector) {
    Box(
        modifier = Modifier
            .padding(end = 6.dp)
            .size(32.dp)
            .background(UmOrange, RoundedCornerShape(6.dp)),
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
private fun SelectedChip(recipient: Recipient, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .background(avatarColor(recipient.email), RoundedCornerShape(50))
            .clickable { onRemove() }
            .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = recipient.name.split(' ').take(2).joinToString(" "),
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

@Composable
private fun SearchResultRow(
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
            text = recipient.name.split(' ').take(2).joinToString(" "),
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
