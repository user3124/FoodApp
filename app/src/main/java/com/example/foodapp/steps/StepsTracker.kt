package com.example.foodapp.steps

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepsTracker @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _todaySteps = MutableStateFlow(loadInitial())
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    private fun loadInitial(): Int =
        if (prefs.getString(KEY_DAY, null) == LocalDate.now().toString()) {
            prefs.getInt(KEY_LAST_STEPS, 0)
        } else {
            0
        }

    /** [total] это показание датчика: шаги с последней перезагрузки устройства. */
    @Synchronized
    fun onSensorTotal(total: Long) {
        val today = LocalDate.now().toString()
        val savedDay = prefs.getString(KEY_DAY, null)
        var baseline = prefs.getLong(KEY_BASELINE, -1L)
        var offset = prefs.getInt(KEY_OFFSET, 0)
        val lastSteps = prefs.getInt(KEY_LAST_STEPS, 0)

        when {
            // Новый день или первый запуск: считаем с нуля
            savedDay != today || baseline < 0 -> {
                baseline = total
                offset = 0
            }
            // Показание стало меньше базы: телефон перезагружался, датчик обнулился
            total < baseline -> {
                offset = lastSteps
                baseline = 0
            }
        }

        val steps = (offset + (total - baseline)).toInt().coerceAtLeast(0)

        prefs.edit()
            .putString(KEY_DAY, today)
            .putLong(KEY_BASELINE, baseline)
            .putInt(KEY_OFFSET, offset)
            .putInt(KEY_LAST_STEPS, steps)
            .apply()

        _todaySteps.value = steps
    }

    private companion object {
        const val PREFS_NAME = "steps_prefs"
        const val KEY_DAY = "day"
        const val KEY_BASELINE = "baseline"
        const val KEY_OFFSET = "offset"
        const val KEY_LAST_STEPS = "last_steps"
    }
}