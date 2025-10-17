package com.example.babel.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.babel.R // Make sure to import your R class

// Define the font file as a Font object
val cormorantGaramond = Font(R.font.cormorantgaramondregular)

// Create the FontFamily using the defined Font object
val cormorantGaramondFamily = FontFamily(cormorantGaramond)

// Then, use the FontFamily in your Typography
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = cormorantGaramondFamily,  // Use the custom font family here
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = cormorantGaramondFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = cormorantGaramondFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
