package com.example.vita.domain.usecase.comida

import com.example.vita.domain.model.Meal
import com.example.vita.domain.repository.MealRepository
import javax.inject.Inject

class RegistrarComidaUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(meal: Meal) {
        mealRepository.insertMeal(meal)
    }
}
