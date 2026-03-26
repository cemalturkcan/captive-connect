package com.cemalturkcan.captiveconnect.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_CARD
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_INPUT

val APP_SHAPES = Shapes(
    small = RoundedCornerShape(RADIUS_INPUT),
    medium = RoundedCornerShape(RADIUS_BUTTON),
    large = RoundedCornerShape(RADIUS_CARD),
)
