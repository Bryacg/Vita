package com.example.vita.domain.usecase.comida

import com.example.vita.domain.model.Meal
import com.example.vita.domain.repository.MealRepository
import javax.inject.Inject

/**
 * Obtiene las comidas registradas hoy por el usuario.
 */
class ObtenerComidasHoyUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(uid: String): List<Meal> {
        return mealRepository.getMealsByDate(uid, System.currentTimeMillis())
    }
}