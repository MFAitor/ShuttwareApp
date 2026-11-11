package com.example.shuttwareapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificacionHelper {

    fun mostrarNotificacion(
        context: Context,
        channelId: String,
        nombreCanal: String,
        titulo: String,
        mensaje: String,
        idNotificacion: Int
    ) {

        //Obtencion del servicio de notificaciones
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                nombreCanal,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            manager.createNotificationChannel(channel)
        }

        //Preparar la notificacion
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setAutoCancel(true)
            .build()

        //Mostrar la notificacion
        manager.notify(idNotificacion, notification)
    }
}