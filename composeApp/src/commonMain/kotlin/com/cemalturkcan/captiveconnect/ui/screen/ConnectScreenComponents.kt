package com.cemalturkcan.captiveconnect.ui.screen

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import captiveconnect.composeapp.generated.resources.Res
import captiveconnect.composeapp.generated.resources.button_connect
import captiveconnect.composeapp.generated.resources.button_connected
import captiveconnect.composeapp.generated.resources.button_retry
import captiveconnect.composeapp.generated.resources.country_code_placeholder
import captiveconnect.composeapp.generated.resources.phone_placeholder
import com.cemalturkcan.captiveconnect.domain.model.ConnectionState
import com.cemalturkcan.captiveconnect.ui.primitives.AppTextField
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_DIM
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_SECONDARY
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_COUNTRY_CODE_WIDTH
import org.jetbrains.compose.resources.stringResource

internal const val COUNTRY_CODE_PREFIX = "+"
internal const val MAX_COUNTRY_CODE_LENGTH = 3

@Composable
internal fun resolveButtonText(state: ConnectionState): String = when (state) {
    is ConnectionState.Connected,
    is ConnectionState.AlreadyOnline -> stringResource(Res.string.button_connected)
    is ConnectionState.Error -> stringResource(Res.string.button_retry)
    else -> stringResource(Res.string.button_connect)
}

@Composable
internal fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = COLOR_TEXT_DIM,
    )
}

@Composable
internal fun CountryCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit,
) {
    AppTextField(
        value = value,
        onValueChange = { if (it.length <= MAX_COUNTRY_CODE_LENGTH) onValueChange(it) },
        placeholder = stringResource(Res.string.country_code_placeholder),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = Modifier.width(SIZE_COUNTRY_CODE_WIDTH),
        prefix = {
            Text(
                text = COUNTRY_CODE_PREFIX,
                style = MaterialTheme.typography.bodyMedium,
                color = COLOR_TEXT_SECONDARY,
            )
        },
    )
}

@Composable
internal fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = stringResource(Res.string.phone_placeholder),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        modifier = modifier,
    )
}
