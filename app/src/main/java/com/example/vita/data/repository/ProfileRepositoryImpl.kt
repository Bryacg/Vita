package com.example.vita.data.repository

import com.example.vita.data.local.dao.ProfileDao
import com.example.vita.data.local.entities.ProfileEntity
import com.example.vita.data.mapper.toDomain
import com.example.vita.data.mapper.toEntity
import com.example.vita.domain.model.Profile
import com.example.vita.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {

    override suspend fun getProfileByUserId(userId: String): Profile? {
        // Usamos el mapper toDomain()
        return profileDao.getProfileByUser(userId)?.toDomain()
    }

    override suspend fun saveProfile(profile: Profile) {
        // Usamos el mapper toEntity()
        profileDao.insertProfile(profile.toEntity())
    }

    override suspend fun deleteProfileByUserId(userId: String) {
        profileDao.deleteProfileByUserId(userId)
    }
}