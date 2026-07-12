package com.example.vita.godot

import android.content.Context
import android.content.Intent

object VitaGodotBridge {

    /**
     * Método estático para ser llamado desde Godot vía JNI/ClassDB
     * @param context El contexto de la actividad de Godot (se pasa desde GDScript)
     * @param resultado "GANASTE" o "PERDISTE"
     */
    @JvmStatic
    fun sendGameResult(context: Context, resultado: String) {
        try {
            val intent = Intent("com.example.vita.GAME_RESULT") // Acción única
            intent.putExtra("score_result", resultado)
            intent.setPackage("com.example.vita") // Asegura que solo tu app lo reciba

            context.sendBroadcast(intent)
            println("📡 Broadcast enviado desde Plugin: $resultado")
        } catch (e: Exception) {
            println("❌ Error enviando broadcast: ${e.message}")
        }
    }
}