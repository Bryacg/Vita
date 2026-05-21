package com.example.vita.domain.usecase.godot

import android.content.Context
import android.content.pm.PackageManager
import com.example.vita.domain.repository.GameRepository
import javax.inject.Inject

class LanzarGodotGameUseCase @Inject constructor(
    private val context: Context,
    private val gameRepository: GameRepository
) {
    /**
     * Valida si el APK de Godot está instalado y listo para ser lanzado
     */
    suspend operator fun invoke(packageName: String): Result {
        return try {
            // Verificar si el APK está instalado
            val isInstalled = isPackageInstalled(packageName)
            if (!isInstalled) {
                return Result.PackageNotInstalled
            }

            // Obtener el intent para lanzar la app
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent == null) {
                return Result.CannotLaunchPackage
            }

            Result.Success(launchIntent)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Valida si un package está instalado en el dispositivo
     */
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    sealed class Result {
        data class Success(val intent: android.content.Intent) : Result()
        object PackageNotInstalled : Result()
        object CannotLaunchPackage : Result()
        data class Error(val message: String) : Result()
    }
}