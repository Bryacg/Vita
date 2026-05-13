package com.example.vita.domain.usecase.progreso

import com.example.vita.domain.model.Achievement
import com.example.vita.domain.model.Meal
import com.example.vita.domain.model.Progress
import com.example.vita.domain.model.Profile
import com.example.vita.domain.repository.FoodRepository
import com.example.vita.domain.repository.MealRepository
import com.example.vita.domain.repository.ProfileRepository
import com.example.vita.domain.repository.ProgresoRepository
import com.example.vita.domain.usecase.achievement.EvaluarLogrosUseCase
import com.example.vita.domain.usecase.perfil.ManageReminderUseCase
import javax.inject.Inject

class ObtenerResumenProgresoUseCase @Inject constructor(
    private val progresoRepository: ProgresoRepository,
    private val mealRepository: MealRepository,
    private val profileRepository: ProfileRepository,
    private val foodRepository: FoodRepository,
    private val evaluarLogrosUseCase: EvaluarLogrosUseCase,
    private val manageReminderUseCase: ManageReminderUseCase
) {
    data class ResumenProgreso(
        val semana: List<Progress>,
        val totalComidas: Int,
        val logros: List<Achievement>,
        val imc: Float,
        val perfil: Profile?
    )

    suspend operator fun invoke(uid: String, nivelActual: Int): ResumenProgreso {
        val comidas      = mealRepository.getMealsByUser(uid)
        val perfil       = profileRepository.getProfileByUserId(uid)
        val semana       = progresoRepository.getProgresoUltimaSemana(uid)
        val preferencias = foodRepository.getUserFoodPreferences(uid)
        val (aguaActivo, _) = manageReminderUseCase.obtenerEstadoGuardado("agua")

        val logros = evaluarLogrosUseCase(
            uid                  = uid,
            nivelActual          = nivelActual,
            cantidadPreferencias = preferencias.size,
            tienePerfilCompleto  = perfil != null && perfil.weight > 0f,
            aguaActiva           = aguaActivo
        )

        val imc = if (perfil != null && perfil.weight > 0f && perfil.height > 0f) {
            val alturaMetros = perfil.height / 100f
            perfil.weight / (alturaMetros * alturaMetros)
        } else 0f

        return ResumenProgreso(
            semana       = semana,
            totalComidas = comidas.size,
            logros       = logros,
            imc          = imc,
            perfil       = perfil
        )
    }
}