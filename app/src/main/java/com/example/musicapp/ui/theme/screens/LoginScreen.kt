// Экран авторизации: ввод имени пользователя и пароля
@file:Suppress("UnusedImport")
package com.example.musicapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.ui.theme.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),        // ViewModel для управления состоянием аутентификации
    onLoginSuccess: () -> Unit,                    // коллбэк при успешном входе
    onRegisterNav: () -> Unit                      // навигация на экран регистрации
) {
    // Состояния текстовых полей
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    // Вертикальный контейнер по центру экрана
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Поле ввода имени пользователя
        OutlinedTextField(
            value = username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(Modifier.height(16.dp))

        // Поле ввода пароля с действием клавиши "Done"
        OutlinedTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (viewModel.canLogin()) viewModel.login(onLoginSuccess)
            })
        )
        Spacer(Modifier.height(24.dp))

        // Кнопка входа, активируется при валидности ввода
        Button(
            onClick = { viewModel.login(onLoginSuccess) },
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.canLogin()
        ) {
            Text("Войти")
        }
        Spacer(Modifier.height(16.dp))

        // Ссылка для перехода на экран регистрации
        Text(
            text = "Новый пользователь? Зарегистрироваться",
            modifier = Modifier.clickable(onClick = onRegisterNav)
        )
    }
}
