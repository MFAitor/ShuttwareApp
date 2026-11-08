package com.example.shuttwareapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shuttwareapp.api.ApiService
import com.example.shuttwareapp.api.Red

class HuecosWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = applicationContext.getSharedPreferences("shuttware", Context.MODE_PRIVATE)
            val ultimoId = prefs.getInt("ultimo_id_hueco", 0)

            // Llamada a la API
            val huecos = Red.api.obtenerHuecosLibres(ultimoId)

            if (huecos.isNotEmpty()) {
                // Guardamos el id más alto visto
                val maxId = huecos.maxOf { it.id_hueco }
                prefs.edit().putInt("ultimo_id_hueco", maxId).apply()

                mostrarNotificacion(huecos.size)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun mostrarNotificacion(cantidad: Int) {
        val channelId = "huecos_nuevos"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Huecos disponibles",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Cambia por tu icono
            .setContentTitle("Nuevos huecos disponibles")
            .setContentText("Hay $cantidad hueco${if (cantidad > 1) "s" else ""} libre${if (cantidad > 1) "s" else ""}")
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}