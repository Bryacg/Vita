package com.example.vita.data.local.datasource

import android.content.Context
import java.io.File

class GodotGameDataSource(private val context: Context) {
    private val RESULT_FILE_NAME = "game_result.txt"

    /**
     * Lee el resultado del juego escrito por Godot
     */
    fun readGameResult(): String? {
        return try {
            val archivo = File(context.getExternalFilesDir(null), RESULT_FILE_NAME)
            if (archivo.exists()) {
                val resultado = archivo.readText().trim()
                archivo.delete() // Limpiar después de leer
                resultado.ifBlank { null }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Escribe un resultado en el archivo (para testing)
     */
    fun writeGameResult(result: String) {
        try {
            val archivo = File(context.getExternalFilesDir(null), RESULT_FILE_NAME)
            archivo.writeText(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Limpia el archivo de resultado
     */
    fun clearGameResult() {
        try {
            val archivo = File(context.getExternalFilesDir(null), RESULT_FILE_NAME)
            if (archivo.exists()) {
                archivo.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}