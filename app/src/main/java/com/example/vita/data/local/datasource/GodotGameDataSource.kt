package com.example.vita.data.local.datasource

import android.os.Environment
import java.io.File

class GodotGameDataSource {
    companion object {
        private const val RESULT_FILE_NAME = "game_result.txt"
        private val RESULT_DIR get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    }

    fun readGameResult(): String? {
        return try {
            val archivo = File(RESULT_DIR, RESULT_FILE_NAME)
            if (archivo.exists()) {
                val resultado = archivo.readText().trim()
                archivo.delete()
                resultado.ifBlank { null }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun writeGameResult(result: String) {
        try {
            val archivo = File(RESULT_DIR, RESULT_FILE_NAME)
            archivo.writeText(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearGameResult() {
        try {
            val archivo = File(RESULT_DIR, RESULT_FILE_NAME)
            if (archivo.exists()) {
                archivo.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}