package com.log.indrop.domain.services.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.log.indrop.data.storage.UserPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Получатель широковещательного намерения о завершении загрузки устройства.
 * Используется для автоматического запуска сервиса уведомлений после перезагрузки устройства.
 */
class BootCompletedReceiver : BroadcastReceiver(), KoinComponent {

    private val userPreferences: UserPreferences by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Устройство загружено, проверка наличия авторизованного пользователя")

            // Проверяем, есть ли авторизованный пользователь
            val userId = userPreferences.getUserId()
            if (userId.isNotEmpty()) {
                Log.d("BootReceiver", "Пользователь найден, запуск сервиса уведомлений")

                // Запускаем сервис уведомлений
                val serviceIntent = Intent(context, NotificationSubscriptionService::class.java).apply {
                    putExtra(NotificationSubscriptionService.EXTRA_USER_ID, userId)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}