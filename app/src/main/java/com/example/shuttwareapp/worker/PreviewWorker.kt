package com.example.shuttwareapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shuttwareapp.api.Red


class PreviewsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {

            //Recuperamos los datos de la aplicacion
            val prefs = applicationContext.getSharedPreferences("shuttware", Context.MODE_PRIVATE)
            val ultimoId = prefs.getInt("ultimo_id_preview", 0)
            val idUsuario = prefs.getInt("id_usuario", 0)

            //Si no hay ningun usuario guardado, no hacemos nada
            if (idUsuario == 0) {
                return Result.success()
            }

            //Consultamos si hay previews nuevas
            val previews = Red.api.obtenerPreviewsNuevas(idUsuario, ultimoId)

            if (previews.isNotEmpty()) {

                //Guardamos el id mas alto recibido
                val maxId = previews.maxOf { it.id }
                prefs.edit().putInt("ultimo_id_preview", maxId).apply()

                //Mostramos una notificacion con la cantidad de previews nuevas
                NotificacionHelper().mostrarNotificacion(
                    applicationContext,
                    "previews_nuevas",
                    "Previews nuevas",
                    "Nuevas previews",
                    "Tienes ${previews.size} preview${if (previews.size > 1) "s" else ""} nueva${if (previews.size > 1) "s" else ""}",
                    1002
                )
            }

            Result.success()

        } catch (e: Exception) {

            //Si ocurre algun error se vuelve a intentar
            Result.retry()
        }
    }
}