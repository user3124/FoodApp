package com.example.foodapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FoodOrange = Color(0xFFF07010)
private val FoodOrangeLight = Color(0xFFFFE6D0)
private val FoodBackground = Color(0xFFFBFAF8)
private val FoodCard = Color(0xFFF1F0EE)
private val FoodText = Color(0xFF2A2A2A)
private val FoodTextSecondary = Color(0xFF6E6E6E)

private val FoodColorScheme = lightColorScheme(
    primary = FoodOrange,
    onPrimary = Color.White,
    primaryContainer = FoodOrangeLight,
    onPrimaryContainer = FoodText,
    background = FoodBackground,
    onBackground = FoodText,
    surface = FoodBackground,
    onSurface = FoodText,
    surfaceVariant = FoodCard,
    onSurfaceVariant = FoodTextSecondary
)

@Composable
fun FoodTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = FoodColorScheme, content = content)
}