package com.example.foodapp.ui.profile

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.foodapp.R
import com.example.foodapp.core.domain.model.Bmi
import com.example.foodapp.core.domain.usecase.CalculateBmiUseCase
import com.example.foodapp.databinding.FragmentProfileBinding
import com.example.foodapp.ui.DietViewModel
import com.example.foodapp.ui.diary.DiaryFragment
import com.example.foodapp.ui.theme.FoodTheme
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var calculateBmi: CalculateBmiUseCase

    private val viewModel: DietViewModel by activityViewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Compose реактивно читает это состояние
    private val bmi = mutableStateOf<Bmi?>(null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.bmiComposeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                FoodTheme { BmiCard(bmi = bmi.value) }
            }
        }

        binding.etHeight.doAfterTextChanged {
            binding.tilHeight.error = null
            updateBmi()
        }
        binding.etWeight.doAfterTextChanged {
            binding.tilWeight.error = null
            updateBmi()
        }

        binding.btnSave.setOnClickListener { onSaveClicked() }
        binding.btnOpenDiary.setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container, DiaryFragment())
                addToBackStack(null)
            }
        }

        // Подставляем сохранённый профиль, если поля ещё пустые
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profile.collect { profile ->
                    if (profile != null &&
                        binding.etHeight.text.isNullOrEmpty() &&
                        binding.etWeight.text.isNullOrEmpty()
                    ) {
                        binding.etHeight.setText(profile.heightCm.toString())
                        binding.etWeight.setText(formatWeight(profile.weightKg))
                    }
                }
            }
        }
    }

    private fun readHeight(): Int? =
        binding.etHeight.text?.toString()?.toIntOrNull()

    private fun readWeight(): Double? =
        binding.etWeight.text?.toString()?.replace(',', '.')?.toDoubleOrNull()

    private fun updateBmi() {
        val height = readHeight()
        val weight = readWeight()
        bmi.value = if (height != null && weight != null) {
            calculateBmi(height, weight)
        } else {
            null
        }
    }

    private fun onSaveClicked() {
        val height = readHeight()?.takeIf { it in 100..250 }
        val weight = readWeight()?.takeIf { it in 30.0..300.0 }

        binding.tilHeight.error = if (height == null) getString(R.string.error_height) else null
        binding.tilWeight.error = if (weight == null) getString(R.string.error_weight) else null

        if (height != null && weight != null) {
            viewModel.saveProfile(height, weight)
            Snackbar.make(binding.root, R.string.profile_saved, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun formatWeight(kg: Double): String =
        if (kg % 1.0 == 0.0) kg.toInt().toString() else kg.toString()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}