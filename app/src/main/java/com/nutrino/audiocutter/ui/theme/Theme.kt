package com.nutrino.audiocutter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutrino.audiocutter.Constants.Colors as AppColors
import com.nutrino.audiocutter.presentation.ViewModel.UserPrefViewModel

private val AppBlack = Color(0xFF000000)
private val AppWhite = Color(0xFFFFFFFF)

private fun appColorPalette(accent: Color) = darkColorScheme(
    primary = accent,
    onPrimary = AppBlack,
    primaryContainer = AppBlack,
    onPrimaryContainer = AppWhite,

    secondary = accent,
    onSecondary = AppBlack,
    secondaryContainer = AppBlack,
    onSecondaryContainer = AppWhite,

    tertiary = accent,
    onTertiary = AppBlack,
    tertiaryContainer = AppBlack,
    onTertiaryContainer = AppWhite,

    background = AppBlack,
    onBackground = AppWhite,
    surface = AppBlack,
    onSurface = AppWhite,
    surfaceVariant = AppBlack,
    onSurfaceVariant = AppWhite,

    inverseSurface = AppBlack,
    inverseOnSurface = AppWhite,
    inversePrimary = accent,

    error = Color(0xFFFF5252),
    onError = AppBlack,
    errorContainer = AppBlack,
    onErrorContainer = Color(0xFFFF5252),

    outline = accent,
    outlineVariant = accent,
    scrim = AppBlack
)

private val redColorPallete = appColorPalette(accent = Color(0xFFFF0B55))
private val greenColorPallete = appColorPalette(accent = Color(0xFF8BC34A))
private val blueColorPallete = appColorPalette(accent = Color(0xFF03A9F4))
private val yellowColorPallete = appColorPalette(accent = Color(0xFFFFEB3B))
private val purpleColorPallete = appColorPalette(accent = Color(0xFFDF77EE))
private val pinkColorPallete = appColorPalette(accent = Color(0xFFF35389))
private val orangeColorPallete = appColorPalette(accent = Color(0xFFF54E1B))


@Composable
fun AudioCutterTheme(
    themeViewModel: UserPrefViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val selectedTheme = themeViewModel.themeSelection.collectAsStateWithLifecycle().value

    val colorScheme = when (selectedTheme) {
        AppColors.REDTHEME -> redColorPallete
        AppColors.GREENTHEME -> greenColorPallete
        AppColors.BLUETHEME -> blueColorPallete
        AppColors.YELLOWTHEME -> yellowColorPallete
        AppColors.PURPLETHEME -> purpleColorPallete
        AppColors.PINKTHEME -> pinkColorPallete
        AppColors.ORANGETHEME -> orangeColorPallete
        else -> orangeColorPallete
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}