package com.cemalturkcan.captiveconnect.ui.primitives

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER_FOCUS
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER_MID
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_SURFACE
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_SURFACE_ELEVATED
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_DIM
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_PRIMARY
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_INPUT

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    prefix: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = COLOR_TEXT_DIM,
            )
        },
        prefix = prefix,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(RADIUS_INPUT),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = COLOR_TEXT_PRIMARY,
            unfocusedTextColor = COLOR_TEXT_PRIMARY,
            cursorColor = COLOR_TEXT_PRIMARY,
            focusedBorderColor = COLOR_BORDER_FOCUS,
            unfocusedBorderColor = COLOR_BORDER_MID,
            focusedContainerColor = COLOR_SURFACE_ELEVATED,
            unfocusedContainerColor = COLOR_SURFACE,
        ),
    )
}
