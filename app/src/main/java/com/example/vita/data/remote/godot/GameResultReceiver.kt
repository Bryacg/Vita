package com.example.vita.data.remote.godot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class GameResultReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val result = intent.getStringExtra("game_result") ?: ""
        val score = intent.getIntExtra("final_score", 0)
        val timeLeft = intent.getIntExtra("time_left", 0)

        Log.d("GameResultReceiver", "Recibido: $result | score=$score | time=$timeLeft")

        // Actualizar UI / DB / lanzar notificación según necesites
        Toast.makeText(
            context,
            "Resultado: $result (Score: $score)",
            Toast.LENGTH_LONG
        ).show()
    }
}