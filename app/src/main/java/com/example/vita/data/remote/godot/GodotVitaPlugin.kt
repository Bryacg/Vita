package com.example.vita.data.remote.godot

import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GodotVitaPlugin(godot: Godot) : GodotPlugin(godot) {

    override fun getPluginName() = "GodotAndroid"

    var onResultado: ((String) -> Unit)? = null

    @UsedByGodot
    fun saveGameResult(gameName: String, result: String) {
        GameResultBuffer.ultimoResultado = result
        onResultado?.invoke(result)
    }

    // ── NUEVA FUNCIÓN — la que llama tu script .gd ──────────────────────
    @UsedByGodot
    fun enviarResultadoYTerminar(resultado: String) {
        val activity = activity ?: return
        activity.runOnUiThread {
            val intent = android.content.Intent("com.example.vita.GAME_RESULT")
            intent.putExtra("game_result", resultado)
            activity.sendBroadcast(intent)
            activity.finish()
        }
    }
}

object GameResultBuffer {
    var ultimoResultado: String? = null
}