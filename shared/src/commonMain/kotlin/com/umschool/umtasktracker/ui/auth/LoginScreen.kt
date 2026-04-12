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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.umschool.umtasktracker.domain.model.UserRole
import com.umschool.umtasktracker.presentation.auth.LoginUiState
import com.umschool.umtasktracker.presentation.auth.LoginViewModel
import com.umschool.umtasktracker.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import umtasktracker.shared.generated.resources.*

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole, Boolean) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val state = uiState as LoginUiState.Success
            onLoginSuccess(state.role, state.isApproved)
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
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_umschool_logo),
                contentDescription = "Логотип Умскул",
                modifier = Modifier.height(72.dp),
                contentScale = ContentScale.FillHeight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.app_name),
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
                    .padding(top = 48.dp)
                    .zIndex(1f)
                    .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                    .background(CardBackground)
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.autorisation_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = stringResource(Res.string.email_label),
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
                            text = stringResource(Res.string.password_label),
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
                            text = if (passwordVisible) stringResource(Res.string.hide_password_text) else stringResource(Res.string.show_password_text),
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
                        onDone = {
                            focusManager.clearFocus()
                            if (email.isNotBlank() && password.isNotBlank()) {
                                onLogin(email.trim(), password)
                            }
                        }
                    ),
                    colors = authFieldColors(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDark),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

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
                        Text(
                            text = stringResource(Res.string.login_link_text),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.account_not_exists_text),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextHint
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.register_button_text),
                        style = MaterialTheme.typography.labelSmall,
                        color = LinkCyan,
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
