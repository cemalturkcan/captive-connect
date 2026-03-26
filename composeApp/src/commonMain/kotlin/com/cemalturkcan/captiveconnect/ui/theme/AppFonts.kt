package com.cemalturkcan.captiveconnect.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import captiveconnect.composeapp.generated.resources.Res
import captiveconnect.composeapp.generated.resources.bebas_neue_regular
import captiveconnect.composeapp.generated.resources.dm_mono_medium
import captiveconnect.composeapp.generated.resources.dm_mono_regular
import captiveconnect.composeapp.generated.resources.dm_sans_medium
import captiveconnect.composeapp.generated.resources.dm_sans_regular
import org.jetbrains.compose.resources.Font

@Composable
fun dmMonoFamily(): FontFamily = FontFamily(
    Font(Res.font.dm_mono_regular, FontWeight.Normal),
    Font(Res.font.dm_mono_medium, FontWeight.Medium),
)

@Composable
fun bebasNeueFamily(): FontFamily = FontFamily(
    Font(Res.font.bebas_neue_regular, FontWeight.Normal),
)

@Composable
fun dmSansFamily(): FontFamily = FontFamily(
    Font(Res.font.dm_sans_regular, FontWeight.Normal),
    Font(Res.font.dm_sans_medium, FontWeight.Medium),
)
