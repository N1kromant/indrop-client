package com.log.indrop.domain.services.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.apollographql.apollo3.ApolloClient
import com.example.graphql.MessageNotificationSubscription
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

/**
 * Менеджер подписок для уведомлений о сообщениях.
 * Управляет запуском фонового сервиса и обработкой подписок.
 */
class NotificationSubscriptionManager(private val context: Context) {

    companion object {
        private const val TAG = "NotificationSubManager"
    }

    private val notificationService = NotificationService(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Запускает подписку на уведомления о сообщениях в фоновом режиме,
     * с использованием Foreground Service для работы даже при закрытом приложении.
     */
    fun startBackgroundSubscription(userId: String) {
        Log.d(TAG, "Запуск фоновой подписки на уведомления для пользователя $userId")

        // Создаем Intent для запуска сервиса
        val serviceIntent = Intent(context, NotificationSubscriptionService::class.java).apply {
            putExtra(NotificationSubscriptionService.EXTRA_USER_ID, userId)
        }

        // Запускаем сервис как Foreground Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    /**
     * Останавливает фоновую подписку
     */
    fun stopBackgroundSubscription() {
        Log.d(TAG, "Остановка фоновой подписки на уведомления")
        context.stopService(Intent(context, NotificationSubscriptionService::class.java))
    }

    /**
     * Запускает подписку в рамках текущего жизненного цикла приложения
     * (будет работать только пока приложение запущено)
     */
    fun startForegroundSubscription(userId: String, apolloClient: ApolloClient) {
        Log.d(TAG, "Запуск подписки на уведомления в рамках приложения для пользователя $userId")

        scope.launch {
            try {
                val subscription = MessageNotificationSubscription(userId = userId)

                apolloClient.subscription(subscription)
                    .toFlow()
                    .onEach { response ->
                        if (response.hasErrors()) {
                            Log.e(TAG, "Ошибка в подписке: ${response.errors}")
                        } else if (response.data != null) {
                            val notification = response.data!!.messageNotification
                            Log.d(TAG, "Получено новое уведомление о сообщении: ${notification.messagePreview}")

                            // Отправка уведомления пользователю
                            notificationService.showChatMessageNotification(
                                chatId = notification.chatId,
                                senderName = notification.senderLogin,
                                message = notification.messagePreview
                            )
                        }
                    }
                    .catch { e ->
                        if (e is CancellationException) {
                            Log.d(TAG, "Подписка отменена")
                        } else {
                            Log.e(TAG, "Ошибка в подписке: ${e.message}", e)
                        }
                    }
                    .onCompletion { cause ->
                        if (cause != null) {
                            Log.d(TAG, "Подписка завершена с ошибкой: ${cause.message}")
                        } else {
                            Log.d(TAG, "Подписка завершена успешно")
                        }
                    }
                    .launchIn(this)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при запуске подписки: ${e.message}", e)
            }
        }
    }
}