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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Автовход после регистрации — переход на главный экран
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
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Выбранные значения в dropdown'ах
    var selectedRole by remember { mutableStateOf<CatalogItem?>(null) }
    var selectedSubject by remember { mutableStateOf<CatalogItem?>(null) }
    var selectedDepartment by remember { mutableStateOf<CatalogItem?>(null) }

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
        // === Верхняя часть: логотип + подпись ===
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
                text = "Умная система мониторинга задач",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                textAlign = TextAlign.Center
            )
        }

        // === «Голова» медведя: уши + карточка ===
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 180.dp)
        ) {
            // Левое ухо
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .align(Alignment.TopCenter)
                    .offset(x = (-140).dp)
                    .zIndex(0f)
                    .clip(CircleShape)
                    .background(CardBackground)
            )
            // Правое ухо
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .align(Alignment.TopCenter)
                    .offset(x = 140.dp)
                    .zIndex(0f)
                    .clip(CircleShape)
                    .background(CardBackground)
            )

            // Карточка-голова
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
                // Заголовок
                Text(
                    text = "Регистрация",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Показываем ошибку загрузки каталогов
                val catalogsError = uiState.catalogsError
                if (catalogsError != null) {
                    Text(
                        text = catalogsError,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onRetryLoadCatalogs) {
                        Text("Повторить загрузку", color = GradientTop)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Индикатор загрузки каталогов
                if (uiState.isCatalogsLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = GradientTop,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Загрузка данных...",
                        color = TextHint,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Имя
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = {
                        Text(text = "Имя", fontSize = 16.sp, color = TextDark)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = registerFieldColors(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Фамилия
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = {
                        Text(text = "Фамилия", fontSize = 16.sp, color = TextDark)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = registerFieldColors(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(text = "Email", fontSize = 16.sp, color = TextDark)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = registerFieldColors(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Пароль
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(text = "Пароль", fontSize = 16.sp, color = TextDark)
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (passwordVisible) "Скрыть" else "Показать",
                            color = GradientTop,
                            fontSize = 12.sp,
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
                    colors = registerFieldColors(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown: Роль
                CatalogDropdown(
                    label = "Роль",
                    items = uiState.roles,
                    selectedItem = selectedRole,
                    onItemSelected = { selectedRole = it },
                    enabled = !uiState.isCatalogsLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown: Предмет
                CatalogDropdown(
                    label = "Предмет",
                    items = uiState.subjects,
                    selectedItem = selectedSubject,
                    onItemSelected = { selectedSubject = it },
                    enabled = !uiState.isCatalogsLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown: Направление
                CatalogDropdown(
                    label = "Направление",
                    items = uiState.departments,
                    selectedItem = selectedDepartment,
                    onItemSelected = { selectedDepartment = it },
                    enabled = !uiState.isCatalogsLoading
                )

                // Ошибка отправки
                val submitError = uiState.submitError
                if (submitError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = submitError,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Кнопка «Зарегистрироваться»
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
                            text = "Зарегистрироваться",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Ссылка «Уже есть аккаунт? Войти»
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Уже есть учётная запись?",
                        color = TextHint,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Войти",
                        color = LinkCyan,
                        fontSize = 12.sp,
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

// === Компонент выпадающего списка для каталогов ===
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
                Text(text = label, fontSize = 16.sp, color = TextDark)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = registerFieldColors(),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = TextDark),
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
                            fontSize = 14.sp,
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
                            text = "Нет данных",
                            fontSize = 14.sp,
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
private fun registerFieldColors() = TextFieldDefaults.colors(
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
                    CatalogItem(1, "Основной"),
                    CatalogItem(2, "Дополнительный")
                )
            )
        )
    }
}
