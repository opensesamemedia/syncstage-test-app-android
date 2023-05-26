package media.opensesame.syncstagetestappandroid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import media.opensesame.syncstagetestappandroid.R

val JosefinSans = FontFamily(
    Font(resId = R.font.josefin_sans_light, FontWeight.Light),
    Font(resId = R.font.josefin_sans_extralight, FontWeight.ExtraLight),
    Font(resId = R.font.josefin_sans_regular, weight = FontWeight.Normal),
    Font(resId = R.font.josefin_sans_medium, weight = FontWeight.Medium),
    Font(resId = R.font.josefin_sans_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.josefin_sans_bold, weight = FontWeight.Bold),
    Font(resId = R.font.josefin_sans_bold, weight = FontWeight.ExtraBold),
    Font(resId = R.font.josefin_sans_thin, weight = FontWeight.Thin),
)

// Set of Material typography styles to start with
val Typography = Typography().copy(
    displayLarge = Typography().displayLarge.copy(fontFamily = JosefinSans),
    displayMedium = Typography().displayMedium.copy(fontFamily = JosefinSans),
    displaySmall = Typography().displaySmall.copy(fontFamily = JosefinSans),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = JosefinSans),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = JosefinSans),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = JosefinSans),
    titleLarge = Typography().titleLarge.copy(fontFamily = JosefinSans),
    titleMedium = Typography().titleMedium.copy(fontFamily = JosefinSans),
    titleSmall = Typography().titleSmall.copy(fontFamily = JosefinSans),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = JosefinSans),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = JosefinSans),
    bodySmall = Typography().bodySmall.copy(fontFamily = JosefinSans),
    labelLarge = Typography().labelLarge.copy(fontFamily = JosefinSans),
    labelMedium = Typography().labelMedium.copy(fontFamily = JosefinSans),
    labelSmall = Typography().labelSmall.copy(fontFamily = JosefinSans),
)