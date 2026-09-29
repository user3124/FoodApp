package com.example.foodapp.core.domain.usecase

import com.example.foodapp.core.domain.model.UserProfile
import com.example.foodapp.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> = profileRepository.observeProfile()
}

class SaveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile) =
        profileRepository.saveProfile(profile)
}