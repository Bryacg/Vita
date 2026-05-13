package com.example.vita.domain.usecase.comida

import com.example.vita.core.DateTimeUtils
import com.example.vita.domain.model.Meal
import com.example.vita.domain.repository.MealRepository
import javax.inject.Inject

class ObtenerComidasHoyUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(uid: String): List<Meal> {
        return mealRepository.getMealsByDate(uid, DateTimeUtils.getTodayMillis())
    }
}