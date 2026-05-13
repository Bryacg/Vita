package com.example.vita.data.remote.godot

import android.util.Log
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GodotVitaPlugin(godot: Godot) : GodotPlugin(godot) {

    override fun getPluginName() = "GodotAndroid"

    // Callback opcional para integración nativa futura
    var onResultado: ((String) -> Unit)? = null

    @UsedByGodot
    fun saveGameResult(gameName: String, result: String) {
        Log.d("GodotVita", "Recibido desde Godot: $gameName — $result")

        // Guarda en el buffer para que la Screen lo lea al regresar
        GameResultBuffer.ultimoResultado = result

        // También notifica por callback si está registrado
        onResultado?.invoke(result)
    }
}

// Buffer estático que sobrevive al cambio de Activity
// Es el puente entre el plugin de Godot y la Screen de Android
object GameResultBuffer {
    var ultimoResultado: String? = null
}
