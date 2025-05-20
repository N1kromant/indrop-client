package com.log.indrop.domain.services.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.log.indrop.Content.Main
import com.log.indrop.R
import java.util.concurrent.atomic.AtomicInteger

class NotificationService(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "messages"
        const val CHANNEL_NAME = "Канал сообщений"
        const val GROUP_KEY = "com.log.indrop.CHAT_MESSAGES"

        // Генератор уникальных ID для уведомлений
        private val notificationId = AtomicInteger(1)

        // Получение нового уникального ID
        fun getNewNotificationId(): Int = notificationId.incrementAndGet()
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Описание канала уведомлений"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Простое уведомление с уникальным ID
    fun showNotification(title: String, message: String): Int {
        val notificationId = getNewNotificationId()

        val intent = Intent(context, Main::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            // Добавляем в группу для возможности свертывания
            .setGroup(GROUP_KEY)

        showNotificationWithPermissionCheck(notificationId, builder.build())

        return notificationId
    }

    // Уведомление о сообщении чата
    fun showChatMessageNotification(
        chatId: String,
        senderId: String,
        senderName: String,
        message: String,
        senderAvatarResId: Int = R.drawable.app_icon // ID ресурса аватара по умолчанию
    ): Int {
        val notificationId = getNewNotificationId()

        // Intent для открытия конкретного чата
        val intent = Intent(context, Main::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("CHAT_ID", chatId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Создаем объект Person для отправителя (для стиля обмена сообщениями)
        val sender = Person.Builder()
            .setName(senderName)
            .setIcon(IconCompat.createWithResource(context, senderAvatarResId))
            .setKey(senderId)
            .build()

        // Создаем стиль уведомления MessagingStyle для чатов
        val messagingStyle = NotificationCompat.MessagingStyle(sender)
            .addMessage(message, System.currentTimeMillis(), sender)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(senderName)
            .setContentText(message)
            .setStyle(messagingStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            // Группируем по chatId для возможности объединения уведомлений из одного чата
            .setGroup(chatId)

        showNotificationWithPermissionCheck(notificationId, builder.build())

        // Показываем суммарное уведомление для группы
        showSummaryNotification(chatId, senderName)

        return notificationId
    }

    // Суммарное уведомление (для группировки)
    private fun showSummaryNotification(groupKey: String, chatName: String) {
        val intent = Intent(context, Main::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Новые сообщения")
            .setContentText("У вас новые сообщения в $chatName")
            .setSmallIcon(R.drawable.app_icon)
            // Важно для группировки
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        showNotificationWithPermissionCheck(0, summaryNotification)
    }

    // Проверка разрешений и отображение уведомления
    private fun showNotificationWithPermissionCheck(notificationId: Int, notification: Notification) {
        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                    notify(notificationId, notification)
                }
            } else {
                notify(notificationId, notification)
            }
        }
    }

    // Обновление существующего уведомления
    fun updateNotification(notificationId: Int, title: String, message: String) {
        val intent = Intent(context, Main::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        showNotificationWithPermissionCheck(notificationId, builder.build())
    }

    // Отмена уведомления по ID
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    // Отмена всех уведомлений
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}