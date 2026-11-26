package com.example.shuttwareapp

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shuttwareapp.worker.HuecosWorker
import com.example.shuttwareapp.worker.PreviewsWorker
import java.util.concurrent.TimeUnit

class ShuttwareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Programar HuecosWorker cada 15 minutos
        val huecosRequest = PeriodicWorkRequestBuilder<HuecosWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "huecos_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            huecosRequest
        )

        // Programar PreviewsWorker cada 15 minutos
        val previewsRequest = PeriodicWorkRequestBuilder<PreviewsWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "previews_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            previewsRequest
        )
    }
}