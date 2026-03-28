package com.umschool.umtasktracker.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.presentation.auth.LoginUiState
import com.umschool.umtasktracker.presentation.auth.LoginViewModel
import com.umschool.umtasktracker.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess((uiState as LoginUiState.Success).role)
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onLogin = { email, password -> viewModel.login(email, password) },
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState = LoginUiState.Idle,
    onLogin: (String, String) -> Unit = { _, _ -> },
    onNavigateToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val isLoading = uiState is LoginUiState.Loading
    val errorMessage = (uiState as? LoginUiState.Error)?.message

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
        // Абсолютное позиционирование по Figma: уши top=201, голова top=248
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 180.dp) // начало зоны ушей
        ) {
            // Уши — два круга ЗА карточкой
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

            // Карточка-голова — перекрывает нижнюю часть ушей
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp) // отступ от верха ушей до начала карточки
                    .zIndex(1f)
                    .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                    .background(CardBackground)
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок
                Text(
                    text = "Авторизация",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                    colors = loginFieldColors(),
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
                        onDone = {
                            focusManager.clearFocus()
                            if (email.isNotBlank() && password.isNotBlank()) {
                                onLogin(email.trim(), password)
                            }
                        }
                    ),
                    colors = loginFieldColors(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                // Ошибка
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Кнопка «Войти»
                Button(
                    onClick = { onLogin(email.trim(), password) },
                    enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Войти", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Распорка — двигает ссылку регистрации вниз головы
                Spacer(modifier = Modifier.weight(1f))

                // Ссылка регистрации — внутри головы, внизу
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "У Вас нет учётной записи?",
                        color = TextHint,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Зарегистрироваться",
                        color = LinkCyan,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigateToRegister()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun loginFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = GradientTop,
    unfocusedIndicatorColor = GradientTop.copy(alpha = 0.5f),
    cursorColor = GradientTop,
    focusedLabelColor = TextDark,
    unfocusedLabelColor = TextDark
)

@Preview(showBackground = true, showSystemUi = true, name = "Idle")
@Composable
private fun LoginScreenPreview() {
    UmTaskTrackerTheme {
        LoginScreenContent()
    }
}