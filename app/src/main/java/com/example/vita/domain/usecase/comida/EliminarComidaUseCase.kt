package com.example.vita.domain.usecase.comida

import com.example.vita.domain.repository.MealRepository
import javax.inject.Inject

class EliminarComidaUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(mealId: Long) {
        mealRepository.deleteMeal(mealId)
    }
}