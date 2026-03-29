package com.umschool.umtasktracker.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.umschool.umtasktracker.R
import com.umschool.umtasktracker.domain.model.CatalogItem
import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.presentation.auth.RegisterUiState
import com.umschool.umtasktracker.presentation.auth.RegisterViewModel
import com.umschool.umtasktracker.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onLoginSuccess: (UserRole) -> Unit = {},
    onRegistrationSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        val role = uiState.loginSuccess
        if (role != null) {
            onLoginSuccess(role)
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onRegister = { email, password, firstName, lastName, roleId, subjectId, departmentId ->
            viewModel.register(email, password, firstName, lastName, roleId, subjectId, departmentId)
        },
        onRetryLoadCatalogs = { viewModel.loadCatalogs() },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState = RegisterUiState(isCatalogsLoading = false),
    onRegister: (String, String, String, String, Int, Int, Int) -> Unit = { _, _, _, _, _, _, _ -> },
    onRetryLoadCatalogs: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var selectedRole by rememberSaveable { mutableStateOf<CatalogItem?>(null) }
    var selectedSubject by rememberSaveable { mutableStateOf<CatalogItem?>(null) }
    var selectedDepartment by rememberSaveable { mutableStateOf<CatalogItem?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientTop, GradientMiddle, GradientBottom)
                )
            )
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_umschool_logo),
                contentDescription = "Логотип Умскул",
                modifier = Modifier.height(72.dp),
                contentScale = ContentScale.FillHeight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleSmall,
                color = Black,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 180.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .align(Alignment.TopCenter)
                    .offset(x = (-140).dp)
                    .zIndex(0f)
                    .clip(CircleShape)
                    .background(CardBackground)
            )
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .align(Alignment.TopCenter)
                    .offset(x = 140.dp)
                    .zIndex(0f)
                    .clip(CircleShape)
                    .background(CardBackground)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 48.dp)
                    .zIndex(1f)
                    .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                    .background(CardBackground)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.registration_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                val catalogsError = uiState.catalogsError
                if (catalogsError != null) {
                    Text(
                        text = catalogsError,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onRetryLoadCatalogs) {
                        Text(
                            stringResource(R.string.retry_load_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = GradientTop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (uiState.isCatalogsLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = GradientTop,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.loading_data_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextHint
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = {
                            Text(
                                text = stringResource(R.string.first_name_label),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextDark
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = authFieldColors(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDark),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(end = 6.dp)
                    )

                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = {
                            Text(
                                text = stringResource(R.string.last_name_label),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextDark
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = authFieldColors(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDark),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CatalogDropdown(
                    label = stringResource(R.string.role_label),
                    items = uiState.roles,
                    selectedItem = selectedRole,
                    onItemSelected = { selectedRole = it },
                    enabled = !uiState.isCatalogsLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                CatalogDropdown(
                    label = stringResource(R.string.subject_label),
                    items = uiState.subjects,
                    selectedItem = selectedSubject,
                    onItemSelected = { selectedSubject = it },
                    enabled = !uiState.isCatalogsLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                CatalogDropdown(
                    label = stringResource(R.string.department_label),
                    items = uiState.departments,
                    selectedItem = selectedDepartment,
                    onItemSelected = { selectedDepartment = it },
                    enabled = !uiState.isCatalogsLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = stringResource(R.string.email_label),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextDark
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = authFieldColors(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = stringResource(R.string.password_label),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextDark
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (passwordVisible) stringResource(R.string.hide_password_text) else stringResource(R.string.show_password_text),
                            style = MaterialTheme.typography.labelSmall,
                            color = GradientTop,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { passwordVisible = !passwordVisible }
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = authFieldColors(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                val submitError = uiState.submitError
                if (submitError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = submitError,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                val canSubmit = !uiState.isSubmitting &&
                    firstName.isNotBlank() &&
                    lastName.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    selectedRole != null &&
                    selectedSubject != null &&
                    selectedDepartment != null

                Button(
                    onClick = {
                        onRegister(
                            email.trim(),
                            password,
                            firstName.trim(),
                            lastName.trim(),
                            selectedRole!!.id,
                            selectedSubject!!.id,
                            selectedDepartment!!.id
                        )
                    },
                    enabled = canSubmit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GradientTop,
                        contentColor = TextDark,
                        disabledContainerColor = GradientTop.copy(alpha = 0.4f),
                        disabledContentColor = TextDark.copy(alpha = 0.7f)
                    ),
                    contentPadding = PaddingValues(horizontal = 34.dp, vertical = 11.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.register_button_text),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.account_exists_text),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextHint
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.login_link_text),
                        style = MaterialTheme.typography.labelSmall,
                        color = LinkCyan,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigateToLogin()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogDropdown(
    label: String,
    items: List<CatalogItem>,
    selectedItem: CatalogItem?,
    onItemSelected: (CatalogItem) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        TextField(
            value = selectedItem?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDark
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = authFieldColors(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDark),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDark
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
            if (items.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.no_data_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextHint
                        )
                    },
                    onClick = { expanded = false }
                )
            }
        }
    }
}

@Composable
fun authFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = GradientTop,
    unfocusedIndicatorColor = GradientTop.copy(alpha = 0.5f),
    cursorColor = GradientTop,
    focusedLabelColor = TextDark,
    unfocusedLabelColor = TextDark
)

@Preview(showBackground = true, showSystemUi = true, name = "Register - Idle")
@Composable
private fun RegisterScreenPreviewIdle() {
    UmTaskTrackerTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(
                isCatalogsLoading = false,
                roles = listOf(
                    CatalogItem(1, "Куратор"),
                    CatalogItem(2, "Менеджер"),
                    CatalogItem(3, "Наставник")
                ),
                subjects = listOf(
                    CatalogItem(1, "Математика"),
                    CatalogItem(2, "Физика"),
                    CatalogItem(3, "Русский язык")
                ),
                departments = listOf(
                    CatalogItem(1, "ЕГЭ"),
                    CatalogItem(2, "ОГЭ")
                )
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Register - Loading")
@Composable
private fun RegisterScreenPreviewLoading() {
    UmTaskTrackerTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(isCatalogsLoading = true)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Register - Error")
@Composable
private fun RegisterScreenPreviewError() {
    UmTaskTrackerTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(
                isCatalogsLoading = false,
                roles = listOf(CatalogItem(1, "Куратор")),
                subjects = emptyList(),
                departments = emptyList(),
                submitError = "Пользователь с таким email уже существует"
            )
        )
    }
}
