package com.example.vita.domain.usecase.comida

import com.example.vita.domain.model.Meal
import com.example.vita.domain.model.NutritionClassifier
import com.example.vita.domain.model.NutritionResult
import com.example.vita.domain.repository.MealRepository
import com.example.vita.domain.usecase.progreso.AgregarXpUseCase
import javax.inject.Inject

/**
 * Orquesta el registro de una comida:
 * 1. Clasifica automáticamente la calidad nutricional.
 * 2. Guarda el registro en Room.
 * 3. Suma el XP proporcional al progreso global del usuario.
 * Devuelve el NutritionResult para que la UI muestre feedback inmediato.
 */
class RegistrarComidaConXpUseCase @Inject constructor(
    private val mealRepository: MealRepository,
    private val agregarXpUseCase: AgregarXpUseCase
) {
    suspend operator fun invoke(
        uid: String,
        nombre: String,
        calorias: Int
    ): NutritionResult {

        // 1. Clasificación automática
        val resultado = NutritionClassifier.classify(nombre, calorias)

        // 2. Guardar comida con el healthyScore calculado
        mealRepository.insertMeal(
            Meal(
                id           = 0,
                userId       = uid,
                name         = nombre,
                calories     = calorias,
                healthyScore = resultado.healthyScore,
                date         = System.currentTimeMillis()
            )
        )

        // 3. Acumular XP al progreso global
        agregarXpUseCase(uid, resultado.xpEarned)

        return resultado
    }
}