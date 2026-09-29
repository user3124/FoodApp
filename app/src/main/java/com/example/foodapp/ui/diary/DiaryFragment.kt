package com.example.foodapp.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodapp.ui.DietViewModel
import com.example.foodapp.ui.theme.FoodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryFragment : Fragment() {

    private val viewModel: DietViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            FoodTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                DiaryScreen(
                    state = state,
                    messages = viewModel.messages,
                    onAddFood = viewModel::onAddFood,
                    onBack = { parentFragmentManager.popBackStack() }
                )
            }
        }
    }
}