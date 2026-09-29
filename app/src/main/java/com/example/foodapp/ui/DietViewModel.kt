package com.example.foodapp.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.BuildConfig
import com.example.foodapp.R
import com.example.foodapp.core.domain.model.AddMealResult
import com.example.foodapp.core.domain.model.DailySummary
import com.example.foodapp.core.domain.model.Meal
import com.example.foodapp.core.domain.model.UserProfile
import com.example.foodapp.core.domain.usecase.AddMealFromTextUseCase
import com.example.foodapp.core.domain.usecase.ObserveDailySummaryUseCase
import com.example.foodapp.core.domain.usecase.ObserveProfileUseCase
import com.example.foodapp.core.domain.usecase.ObserveTodayMealsUseCase
import com.example.foodapp.core.domain.usecase.SaveProfileUseCase
import com.example.foodapp.steps.StepsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiaryUiState(
    val meals: List<Meal> = emptyList(),
    val summary: DailySummary = DailySummary(0, 0.0, 0.0, 0.0),
    val steps: Int = 0,
    val isLoading: Boolean = false
)

/** Одноразовые сообщения для Snackbar. */
enum class DiaryMessage(@StringRes val textRes: Int) {
    ADDED(R.string.msg_added),
    EMPTY_INPUT(R.string.msg_empty_input),
    NOT_RECOGNIZED(R.string.msg_not_recognized),
    LIMIT_REACHED(R.string.msg_limit_reached)
}

@HiltViewModel
class DietViewModel @Inject constructor(
    observeTodayMeals: ObserveTodayMealsUseCase,
    observeDailySummary: ObserveDailySummaryUseCase,
    observeProfile: ObserveProfileUseCase,
    private val addMealFromText: AddMealFromTextUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    stepsTracker: StepsTracker
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)

    private val _messages = Channel<DiaryMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    val uiState: StateFlow<DiaryUiState> = combine(
        observeTodayMeals(),
        observeDailySummary(),
        stepsTracker.todaySteps,
        isLoading
    ) { meals, summary, steps, loading ->
        DiaryUiState(meals, summary, steps, loading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiaryUiState())

    val profile: StateFlow<UserProfile?> = observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onAddFood(text: String) {
        if (isLoading.value) return
        if (text.isBlank()) {
            _messages.trySend(DiaryMessage.EMPTY_INPUT)
            return
        }
        // Флаг фичи из productFlavors: у free-версии есть дневной лимит
        if (uiState.value.meals.size >= BuildConfig.MAX_DAILY_AI_REQUESTS) {
            _messages.trySend(DiaryMessage.LIMIT_REACHED)
            return
        }

        viewModelScope.launch {
            isLoading.value = true // UI блокируется, показывается индикатор
            try {
                val message = when (addMealFromText(text)) {
                    is AddMealResult.Success -> DiaryMessage.ADDED
                    AddMealResult.EmptyInput -> DiaryMessage.EMPTY_INPUT
                    AddMealResult.NotRecognized -> DiaryMessage.NOT_RECOGNIZED
                }
                _messages.send(message)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun saveProfile(heightCm: Int, weightKg: Double) {
        viewModelScope.launch {
            saveProfileUseCase(UserProfile(heightCm, weightKg))
        }
    }
}