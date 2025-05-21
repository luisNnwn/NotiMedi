package com.example.notimedi.notificaciones

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notimedi.R

class RecordatorioWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val nombre = inputData.getString("nombre") ?: return Result.failure()
        val dosis = inputData.getString("dosis") ?: return Result.failure()
        val unidad = inputData.getString("unidad") ?: ""
        val via = inputData.getString("via") ?: ""

        val notification = NotificationCompat.Builder(applicationContext, "recordatorios_channel")
            .setSmallIcon(R.drawable.ic_notificacion2)
            .setContentTitle("Hora de tomar $nombre")
            .setContentText("Dosis: $dosis $unidad. Vía: $via")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            NotificationManagerCompat.from(applicationContext)
                .notify(System.currentTimeMillis().toInt(), notification)
        }

        return Result.success()
    }
}
