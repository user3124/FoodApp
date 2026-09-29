package com.example.foodapp.core.data.repository

import com.example.foodapp.core.data.local.ProfileDao
import com.example.foodapp.core.data.mapper.toDomain
import com.example.foodapp.core.data.mapper.toEntity
import com.example.foodapp.core.domain.model.UserProfile
import com.example.foodapp.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> =
        profileDao.observe().map { it?.toDomain() }

    override suspend fun saveProfile(profile: UserProfile) {
        profileDao.upsert(profile.toEntity())
    }
}