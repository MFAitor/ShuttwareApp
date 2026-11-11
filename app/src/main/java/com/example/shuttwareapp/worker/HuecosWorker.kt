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

            //Recuperacion de ultimo ID usado
            val prefs = applicationContext.getSharedPreferences("shuttware", Context.MODE_PRIVATE)
            val ultimoId = prefs.getInt("ultimo_id_hueco", 0)

            //Consulta de si hay huecos disponibles
            val huecos = Red.api.obtenerHuecosLibres(ultimoId)

            //Se guarda el ID mas alto si hay varios distintos
            if (huecos.isNotEmpty()) {

                val maxId = huecos.maxOf { it.id_hueco }

                prefs.edit().putInt("ultimo_id_hueco", maxId).apply()

                //Mostrar la notificacion con la cantidad de huecos
                NotificacionHelper().mostrarNotificacion(
                    applicationContext,
                    "huecos_nuevos",
                    "Huecos disponibles",
                    "Nuevos huecos disponibles",
                    "Hay ${huecos.size} hueco${if (huecos.size > 1) "s" else ""} libre${if (huecos.size > 1) "s" else ""}",
                    1001
                )
            }

            Result.success()

        } catch (e: Exception) {
            //Se vuelve a intentar en caso de error
            Result.retry()
        }
    }
}