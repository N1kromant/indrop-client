package com.log.indrop.domain.services.notification

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.log.indrop.data.storage.UserPreferences
import com.log.indrop.domain.subscription.MessageSubscriptionHandler
import com.log.network.ViewModels.MainViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Менеджер для интеграции уведомлений с основным процессом приложения.
 * Связывает MainViewModel с системой подписок на уведомления.
 */
class NotificationIntegrationManager(private val context: Context) : KoinComponent {

    companion object {
        private const val TAG = "NotificationManager"
    }

    private val userPreferences: UserPreferences by inject()
    private val subscriptionHandler = MessageSubscriptionHandler(context)

    /**
     * Активирует подписку на уведомления при успешной авторизации пользователя.
     * Вызывайте этот метод после успешной авторизации.
     *
     * @param userId ID авторизованного пользователя
     * @param lifecycleOwner LifecycleOwner для привязки к жизненному циклу
     */
    fun activateNotifications(userId: String, lifecycleOwner: LifecycleOwner) {
        Log.d(TAG, "Активация уведомлений для пользователя $userId")

        // Сохраняем ID пользователя для возможного восстановления после перезагрузки
        userPreferences.saveUserId(userId)
        userPreferences.setNotificationsEnabled(true)

        // Запускаем подписку в контексте жизненного цикла
        lifecycleOwner.lifecycleScope.launch {
            subscriptionHandler.startMessageNotificationSubscription(userId)
        }
    }

    /**
     * Деактивирует подписку на уведомления.
     * Вызывайте этот метод при выходе пользователя из системы.
     */
    fun deactivateNotifications() {
        Log.d(TAG, "Деактивация уведомлений")

        // Останавливаем подписку
        subscriptionHandler.stopMessageNotificationSubscription()

        // Очищаем данные пользователя при необходимости
        userPreferences.setNotificationsEnabled(false)
    }

    /**
     * Автоматически восстанавливает подписку на уведомления при запуске приложения,
     * если пользователь уже авторизован.
     *
     * @param lifecycleOwner LifecycleOwner для привязки к жизненному циклу
     * @return true, если подписка была восстановлена
     */
    fun restoreNotificationsIfNeeded(lifecycleOwner: LifecycleOwner): Boolean {
        val userId = userPreferences.getUserId()
        val notificationsEnabled = userPreferences.areNotificationsEnabled()

        if (userId.isNotEmpty() && notificationsEnabled) {
            Log.d(TAG, "Восстановление подписки на уведомления для пользователя $userId")

            lifecycleOwner.lifecycleScope.launch {
                subscriptionHandler.startMessageNotificationSubscription(userId)
            }

            return true
        }

        return false
    }

    /**
     * Использует ViewModel для активации уведомлений после авторизации.
     * Это удобный метод, который можно вызвать прямо из Main после успешной авторизации.
     */
    fun setupNotificationsWithViewModel(viewModel: MainViewModel, lifecycleOwner: LifecycleOwner) {
        val userId = viewModel.myId.value

        if (!userId.isNullOrEmpty()) {
            activateNotifications(userId, lifecycleOwner)
        } else {
            Log.w(TAG, "ID пользователя отсутствует в MainViewModel, уведомления не активированы")
        }
    }
}