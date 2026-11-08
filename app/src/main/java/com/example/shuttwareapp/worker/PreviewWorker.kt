package com.example.shuttwareapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shuttwareapp.api.Red


class PreviewsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = applicationContext.getSharedPreferences("shuttware", Context.MODE_PRIVATE)
            val ultimoId = prefs.getInt("ultimo_id_preview", 0)
            val idUsuario = prefs.getInt("id_usuario", 0) // Asegúrate de guardar el id al hacer login

            if (idUsuario == 0) return Result.success()

            val previews = Red.api.obtenerPreviewsNuevas(idUsuario, ultimoId)

            if (previews.isNotEmpty()) {
                val maxId = previews.maxOf { it.id }
                prefs.edit().putInt("ultimo_id_preview", maxId).apply()

                mostrarNotificacion(previews.size)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun mostrarNotificacion(cantidad: Int) {
        val channelId = "previews_nuevas"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Previews nuevas",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Nuevas previews")
            .setContentText("Tienes $cantidad preview${if (cantidad > 1) "s" else ""} nueva${if (cantidad > 1) "s" else ""}")
            .setAutoCancel(true)
            .build()

        manager.notify(1002, notification)
    }
}