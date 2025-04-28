// app/src/main/java/com/example/musicapp/ui/theme/theme/Type.kt
package com.example.musicapp.ui.theme.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.musicapp.R

// Подключаем наши файлы из res/font:
private val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold,    FontWeight.Bold)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily    = Montserrat,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily    = Montserrat,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily    = Montserrat,
        fontWeight    = FontWeight.Bold,
        fontSize      = 12.sp,
        letterSpacing = 0.5.sp
    )
    // при необходимости добавьте остальные TextStyle...
)
