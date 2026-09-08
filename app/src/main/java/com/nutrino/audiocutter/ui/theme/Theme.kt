package com.nutrino.audiocutter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutrino.audiocutter.presentation.ViewModel.RevenueCatViewmodel
import com.nutrino.audiocutter.presentation.ViewModel.UserPrefViewModel
import com.nutrino.audiocutter.Constants.Colors as AppColors

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

// 16 Additional Professional Studio Color Palettes
private val tealColorPallete = appColorPalette(accent = Color(0xFF00E5FF))
private val emeraldColorPallete = appColorPalette(accent = Color(0xFF00E676))
private val indigoColorPallete = appColorPalette(accent = Color(0xFF651FFF))
private val amberColorPallete = appColorPalette(accent = Color(0xFFFFC400))
private val crimsonColorPallete = appColorPalette(accent = Color(0xFFD50000))
private val coralColorPallete = appColorPalette(accent = Color(0xFFFF6E40))
private val limeColorPallete = appColorPalette(accent = Color(0xFFAEEA00))
private val violetColorPallete = appColorPalette(accent = Color(0xFFB388FF))
private val roseColorPallete = appColorPalette(accent = Color(0xFFFF1744))
private val aquaColorPallete = appColorPalette(accent = Color(0xFF1DE9B6))
private val electricBlueColorPallete = appColorPalette(accent = Color(0xFF2979FF))
private val plumColorPallete = appColorPalette(accent = Color(0xFFE040FB))
private val solarColorPallete = appColorPalette(accent = Color(0xFFFF9100))
private val mintColorPallete = appColorPalette(accent = Color(0xFF64FFDA))
private val magentaColorPallete = appColorPalette(accent = Color(0xFFFF007F))
private val silverColorPallete = appColorPalette(accent = Color(0xFFE0E6ED))


@Composable
fun AudioCutterTheme(
    themeViewModel: UserPrefViewModel = hiltViewModel(),
    revenueCatViewmodel: RevenueCatViewmodel = hiltViewModel(),
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
        AppColors.TEALTHEME -> tealColorPallete
        AppColors.EMERALDTHEME -> emeraldColorPallete
        AppColors.INDIGOTHEME -> indigoColorPallete
        AppColors.AMBERTHEME -> amberColorPallete
        AppColors.CRIMSONTHEME -> crimsonColorPallete
        AppColors.CORALTHEME -> coralColorPallete
        AppColors.LIMETHEME -> limeColorPallete
        AppColors.VIOLETTHEME -> violetColorPallete
        AppColors.ROSETHEME -> roseColorPallete
        AppColors.AQUATHEME -> aquaColorPallete
        AppColors.ELECTRICBLUETHEME -> electricBlueColorPallete
        AppColors.PLUMTHEME -> plumColorPallete
        AppColors.SOLARTHEME -> solarColorPallete
        AppColors.MINTTHEME -> mintColorPallete
        AppColors.MAGENTATHEME -> magentaColorPallete
        AppColors.SILVERTHEME -> silverColorPallete
        else -> orangeColorPallete
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
