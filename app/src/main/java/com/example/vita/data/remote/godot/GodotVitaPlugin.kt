package com.example.vita.data.remote.godot

import android.util.Log
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot

class GodotVitaPlugin(godot: Godot) : GodotPlugin(godot) {

    // Este nombre DEBE ser idéntico al que pusiste en Godot
    override fun getPluginName() = "GodotAndroid"

    // Esta función es la que llama tu código: plugin.call("saveGameResult", ...)
    @UsedByGodot
    fun saveGameResult(gameName: String, result: String) {
        Log.d("GodotVita", "Recibido desde Godot: Juego $gameName, Resultado: $result")

        // Aquí guardamos el resultado para que Vita lo use
        // Lo ideal es mandarlo a un Singleton o guardar en SharedPreferences
        GameResultBuffer.ultimoResultado = result
    }
}

// Objeto temporal para guardar el resultado mientras volvemos a la app
object GameResultBuffer {
    var ultimoResultado: String? = null
}