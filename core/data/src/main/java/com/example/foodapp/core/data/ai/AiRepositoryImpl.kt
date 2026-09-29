package com.example.foodapp.core.data.ai

import com.example.foodapp.core.data.mapper.toDomain
import com.example.foodapp.core.domain.model.FoodItem
import com.example.foodapp.core.domain.repository.AiRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.roundToInt

class AiRepositoryImpl @Inject constructor() : AiRepository {

    override suspend fun recognizeFood(text: String): List<FoodItem> {
        delay(NETWORK_DELAY_MS) // имитация сетевого запроса к нейросети
        return parse(text).map { it.toDomain() }
    }

    private fun parse(text: String): List<AiFoodDto> {
        val segments = text.lowercase()
            .split(Regex("[,;+\\n]|\\sи\\s"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return segments.mapNotNull { segment ->
            val words = segment.split(Regex("[^\\p{L}]+")).filter { it.isNotEmpty() }
            val product = PRODUCTS.firstOrNull { p ->
                words.any { word -> p.keywords.any { word.startsWith(it) } }
            } ?: return@mapNotNull null

            val grams = parseGrams(segment, product)
            val factor = grams / 100.0
            AiFoodDto(
                name = product.name,
                grams = grams,
                calories = (product.kcal * factor).roundToInt(),
                protein = round1(product.protein * factor),
                fat = round1(product.fat * factor),
                carbs = round1(product.carbs * factor)
            )
        }
    }

    private fun parseGrams(segment: String, product: Product): Int {
        val match = NUMBER.find(segment)
            ?: return product.pieceGrams ?: DEFAULT_GRAMS

        val amount = match.value.replace(',', '.').toDouble()
        val unit = segment.substring(match.range.last + 1).trim().takeWhile { it.isLetter() }

        val grams = when {
            unit in KG_UNITS -> amount * 1000
            unit in GRAM_UNITS -> amount
            unit in PIECE_UNITS || product.pieceGrams != null ->
                amount * (product.pieceGrams ?: DEFAULT_GRAMS)
            else -> amount
        }
        return grams.roundToInt().coerceIn(1, 5000)
    }

    private fun round1(value: Double) = (value * 10).roundToInt() / 10.0

    /** Значения на 100 г готового продукта. */
    private class Product(
        val name: String,
        val keywords: List<String>,
        val kcal: Double,
        val protein: Double,
        val fat: Double,
        val carbs: Double,
        val pieceGrams: Int? = null
    )

    private companion object {
        const val NETWORK_DELAY_MS = 1500L
        const val DEFAULT_GRAMS = 100

        val NUMBER = Regex("""\d+(?:[.,]\d+)?""")
        val KG_UNITS = setOf("кг", "килограмм", "килограмма", "килограммов")
        val GRAM_UNITS = setOf("г", "гр", "грамм", "грамма", "граммов", "мл")
        val PIECE_UNITS = setOf("шт", "штук", "штуки", "штука")

        val PRODUCTS = listOf(
            Product("Сыр", listOf("сыр"), 350.0, 25.0, 27.0, 0.5),
            Product("Яйцо", listOf("яйц", "яиц"), 157.0, 12.7, 11.5, 0.7, pieceGrams = 50),
            Product("Куриная грудка", listOf("кур", "грудк"), 165.0, 31.0, 3.6, 0.0),
            Product("Гречка", listOf("греч"), 110.0, 4.2, 1.1, 21.3),
            Product("Рис", listOf("рис"), 130.0, 2.7, 0.3, 28.0),
            Product("Овсянка", listOf("овсян"), 88.0, 3.0, 1.7, 15.0),
            Product("Макароны", listOf("макарон", "паст"), 158.0, 5.8, 0.9, 30.9),
            Product("Картофель", listOf("картоф", "картош"), 77.0, 2.0, 0.4, 16.0),
            Product("Говядина", listOf("говяд"), 250.0, 26.0, 15.0, 0.0),
            Product("Лосось", listOf("лосос", "рыб"), 208.0, 20.0, 13.0, 0.0),
            Product("Творог", listOf("творог"), 121.0, 18.0, 5.0, 3.0),
            Product("Йогурт", listOf("йогурт"), 60.0, 4.0, 1.5, 7.0),
            Product("Молоко", listOf("молок"), 52.0, 2.8, 2.5, 4.7),
            Product("Хлеб", listOf("хлеб"), 265.0, 9.0, 3.2, 49.0, pieceGrams = 30),
            Product("Яблоко", listOf("яблок"), 52.0, 0.3, 0.2, 14.0, pieceGrams = 180),
            Product("Банан", listOf("банан"), 89.0, 1.1, 0.3, 22.8, pieceGrams = 120),
            Product("Огурец", listOf("огур"), 15.0, 0.8, 0.1, 2.8, pieceGrams = 100),
            Product("Помидор", listOf("помидор", "томат"), 20.0, 1.1, 0.2, 3.7, pieceGrams = 100)
        )
    }
}