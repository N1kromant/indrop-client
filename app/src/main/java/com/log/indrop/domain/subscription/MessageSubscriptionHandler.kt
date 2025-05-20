package com.log.indrop.domain.subscription

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.log.indrop.domain.services.notification.NotificationSubscriptionManager
import com.log.network.NetworkManager
import kotlinx.coroutines.CancellationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MessageSubscriptionHandler(private val context: Context) : KoinComponent {
    private val networkManager: NetworkManager by inject()
    private lateinit var client: ApolloClient
    private val subscriptionManager = NotificationSubscriptionManager(context)

    /**
     * Запускает подписку на уведомления о сообщениях.
     * Подписка будет работать даже когда приложение в фоне или закрыто.
     */
    suspend fun startMessageNotificationSubscription(userId: String) {
        client = networkManager.apolloClient
        println("Активация подписки на уведомления о сообщениях для пользователя $userId")

        try {
            // Запускаем подписку в фоновом режиме через сервис
            subscriptionManager.startBackgroundSubscription(userId)

            // Также можно запустить подписку в основном приложении,
            // чтобы получать уведомления немедленно, пока приложение активно
            subscriptionManager.startForegroundSubscription(userId, client)

        } catch (e: CancellationException) {
            println("Подписка отменена")
        } catch (e: Exception) {
            println("Ошибка в подписке: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Останавливает подписку на уведомления о сообщениях
     */
    fun stopMessageNotificationSubscription() {
        subscriptionManager.stopBackgroundSubscription()
    }
}