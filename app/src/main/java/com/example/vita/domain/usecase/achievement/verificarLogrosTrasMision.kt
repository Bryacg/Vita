package com.example.vita.domain.usecase.achievement
//
//fun verificarLogrosTrasMision() {
//    viewModelScope.launch(Dispatchers.Default) {
//        val uid = authRepository.getCurrentUserId() ?: return@launch
//
//        // 1. Obtener datos actuales
//        val retosCompletados = challengeRepository.getCompletadosCount(uid)
//        val nivel = uiState.value.user?.nivel ?: 1
//
//        // 2. Evaluar
//        val logrosDesbloqueables = AchievementEvaluator.checkAchievements(
//            totalRetosCompletados = retosCompletados,
//            nivelActual = nivel,
//            retosSemanalesCompletados = 0, // Implementar lógica
//            rachaActual = 0 // Implementar lógica
//        )
//
//        // 3. Guardar en DB solo los nuevos
//        logrosDesbloqueables.forEach { tipo ->
//            achievementRepository.saveAchievement(
//                Achievement(
//                    id = tipo.id,
//                    userId = uid,
//                    name = tipo.title,
//                    description = tipo.desc,
//                    unlocked = true
//                )
//            )
//        }
//    }
//}