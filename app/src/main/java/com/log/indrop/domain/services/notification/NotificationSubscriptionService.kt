package com.log.indrop.domain.services.notification

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.apollographql.apollo3.ApolloClient
import com.example.graphql.MessageNotificationSubscription
import com.log.indrop.R


import com.log.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class NotificationSubscriptionService : Service() {
    companion object {
        private const val TAG = "NotificationService"
        private const val FOREGROUND_SERVICE_ID = 1001
        const val EXTRA_USER_ID = "extra_user_id"
    }

    private val networkManager: NetworkManager by inject()
    private val notificationService: NotificationService by lazy {
        NotificationService(applicationContext)
    }

    private val recentMessages = mutableMapOf<String, MutableList<NotificationCompat.MessagingStyle.Message>>()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var subscriptionJob: Job? = null
    private lateinit var apolloClient: ApolloClient

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Сервис подписки на уведомления создан")
        apolloClient = networkManager.apolloClient
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Сервис подписки на уведомления запущен")

        val userId = intent?.getStringExtra(EXTRA_USER_ID)
        if (userId.isNullOrEmpty()) {
            Log.e(TAG, "ID пользователя не предоставлен. Останавливаем сервис.")
            stopSelf()
            return START_NOT_STICKY
        }

        // Запуск сервиса в режиме foreground с уведомлением
        startForeground(FOREGROUND_SERVICE_ID, createForegroundNotification())

        // Запуск подписки
        startSubscription(userId)

        // Если сервис будет убит системой, пусть попытается перезапуститься
        return START_STICKY
    }

    private fun createForegroundNotification(): Notification {
        // Создаем канал уведомлений, используя существующий NotificationService
        notificationService.createNotificationChannel()

        return NotificationCompat.Builder(this, "foreground_service_channel")
            .setContentTitle("1ndrop активен")
            .setContentText("Получение уведомлений о новых сообщениях")
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startSubscription(userId: String) {
        // Отменяем предыдущую подписку, если она существует
        subscriptionJob?.cancel()

        subscriptionJob = serviceScope.launch {
            Log.d(TAG, "Запуск подписки на уведомления для пользователя $userId")

            val subscription = MessageNotificationSubscription(userId = userId)

            apolloClient.subscription(subscription)
                .toFlow()
                .onEach { response ->
                    if (response.hasErrors()) {
                        Log.e(TAG, "Ошибка в подписке: ${response.errors}")
                    } else if (response.data != null) {
                        val notification = response.data!!.messageNotification
                        Log.d(TAG, "Получено новое уведомление о сообщении: ${notification.messagePreview}")

                        if (!AppVisibilityTracker.isAppInForeground) {
                            // Отправка уведомления пользователю
                            notificationService.showChatMessageNotification(
                                chatId = notification.chatId,
                                senderName = notification.senderLogin,
                                message = notification.messagePreview
                            )
                        }
                    }
                }
                .catch { e ->
                    Log.e(TAG, "Ошибка в подписке: ${e.message}", e)
                    // Перезапускаем подписку при ошибке после небольшой задержки
                    kotlinx.coroutines.delay(5000)
                    startSubscription(userId)
                }
                .onCompletion { cause ->
                    if (cause != null) {
                        Log.d(TAG, "Подписка завершена с ошибкой: ${cause.message}")
                    } else {
                        Log.d(TAG, "Подписка завершена успешно")
                    }
                }
                .launchIn(this)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "Сервис подписки на уведомления уничтожен")
        subscriptionJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}