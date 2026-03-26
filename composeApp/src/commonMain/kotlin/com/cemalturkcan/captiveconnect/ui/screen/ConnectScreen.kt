package com.cemalturkcan.captiveconnect.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import captiveconnect.composeapp.generated.resources.Res
import captiveconnect.composeapp.generated.resources.password
import captiveconnect.composeapp.generated.resources.password_placeholder
import captiveconnect.composeapp.generated.resources.phone_number
import com.cemalturkcan.captiveconnect.domain.model.ConnectionState
import com.cemalturkcan.captiveconnect.presentation.ConnectViewModel
import com.cemalturkcan.captiveconnect.ui.components.StatusIndicator
import com.cemalturkcan.captiveconnect.ui.components.WifiAnimation
import com.cemalturkcan.captiveconnect.ui.primitives.AppTextField
import com.cemalturkcan.captiveconnect.ui.primitives.PrimaryButton
import com.cemalturkcan.captiveconnect.ui.primitives.SettingsIconButton
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_10
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_12
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_2
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_3
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_5
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_6
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_8
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConnectScreen(
    viewModel: ConnectViewModel,
    onOpenSettings: () -> Unit,
) {
    val uiState by viewModel.state.collectAsState()

    val isLoading = uiState.connectionState is ConnectionState.Checking
        || uiState.connectionState is ConnectionState.LoggingIn
        || uiState.connectionState is ConnectionState.Verifying

    val isSuccess = uiState.connectionState is ConnectionState.Connected
        || uiState.connectionState is ConnectionState.AlreadyOnline

    val isError = uiState.connectionState is ConnectionState.Error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding(),
    ) {
        TopBar(onSettingsClick = onOpenSettings)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SPACING_6),
            ) {
                WifiAnimation(connectionState = uiState.connectionState)
                StatusIndicator(connectionState = uiState.connectionState)
            }
        }

        FormArea(
            phoneNumber = uiState.phoneNumber,
            password = uiState.password,
            countryCode = uiState.countryCode,
            connectionState = uiState.connectionState,
            isLoading = isLoading,
            isSuccess = isSuccess,
            isError = isError,
            onPhoneChange = viewModel::updatePhoneNumber,
            onPasswordChange = viewModel::updatePassword,
            onCountryCodeChange = viewModel::updateCountryCode,
            onConnectClick = {
                if (isSuccess) viewModel.resetState() else viewModel.connect()
            },
        )
    }
}

@Composable
private fun TopBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SPACING_8, vertical = SPACING_8),
        horizontalArrangement = Arrangement.End,
    ) {
        SettingsIconButton(onClick = onSettingsClick)
    }
}

@Composable
private fun FormArea(
    phoneNumber: String,
    password: String,
    countryCode: String,
    connectionState: ConnectionState,
    isLoading: Boolean,
    isSuccess: Boolean,
    isError: Boolean,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCountryCodeChange: (String) -> Unit,
    onConnectClick: () -> Unit,
) {
    val phoneFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SPACING_10, vertical = SPACING_12),
        verticalArrangement = Arrangement.spacedBy(SPACING_5),
    ) {
        PhoneSection(
            countryCode = countryCode,
            phoneNumber = phoneNumber,
            onCountryCodeChange = onCountryCodeChange,
            onPhoneChange = onPhoneChange,
            phoneFocus = phoneFocus,
            passwordFocus = passwordFocus,
        )

        PasswordSection(
            password = password,
            onPasswordChange = onPasswordChange,
            passwordFocus = passwordFocus,
            onDone = {
                focusManager.clearFocus()
                onConnectClick()
            },
        )

        Spacer(modifier = Modifier.height(SPACING_2))

        PrimaryButton(
            text = resolveButtonText(connectionState),
            onClick = onConnectClick,
            enabled = phoneNumber.isNotBlank() && password.isNotBlank(),
            isLoading = isLoading,
            isSuccess = isSuccess,
            isError = isError,
        )
    }
}

@Composable
private fun PhoneSection(
    countryCode: String,
    phoneNumber: String,
    onCountryCodeChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    phoneFocus: FocusRequester,
    passwordFocus: FocusRequester,
) {
    Column {
        SectionLabel(text = stringResource(Res.string.phone_number))
        Spacer(modifier = Modifier.height(SPACING_2))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SPACING_3),
            verticalAlignment = Alignment.Top,
        ) {
            CountryCodeField(
                value = countryCode,
                onValueChange = onCountryCodeChange,
                onNext = { phoneFocus.requestFocus() },
            )
            PhoneField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                onNext = { passwordFocus.requestFocus() },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(phoneFocus),
            )
        }
    }
}

@Composable
private fun PasswordSection(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordFocus: FocusRequester,
    onDone: () -> Unit,
) {
    Column {
        SectionLabel(text = stringResource(Res.string.password))
        Spacer(modifier = Modifier.height(SPACING_2))
        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(Res.string.password_placeholder),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocus),
        )
    }
}
