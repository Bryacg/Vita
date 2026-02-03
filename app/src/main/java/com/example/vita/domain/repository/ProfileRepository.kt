package com.example.vita.domain.repository

import com.example.vita.domain.model.Profile

interface ProfileRepository {
    // Obtener el perfil por el ID del usuario
    suspend fun getProfileByUserId(userId: String): Profile?

    // Guardar o actualizar el perfil (biometría)
    suspend fun saveProfile(profile: Profile)

    // Borrar datos (útil para cerrar sesión)
    suspend fun deleteProfileByUserId(userId: String)
}