package com.example.vita.domain.repository

interface GodotGameRepository {

    /**
     * Lee el resultado del juego escrito por Godot desde archivo
     */
    suspend fun readGameResult(): String?

    /**
     * Escribe un resultado en el archivo (para testing/debug)
     */
    suspend fun writeGameResult(result: String)

    /**
     * Limpia el archivo de resultado
     */
    suspend fun clearGameResult()
}