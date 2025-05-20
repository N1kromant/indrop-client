package com.log.indrop.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Класс для работы с пользовательскими настройками и данными.
 */
class UserPreferences(context: Context) {
    companion object {
        private const val PREFS_NAME = "user_preferences"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Сохраняет ID пользователя
     */
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    /**
     * Получает ID пользователя
     */
    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }

    /**
     * Сохраняет логин пользователя
     */
    fun saveUserLogin(login: String) {
        prefs.edit().putString(KEY_USER_LOGIN, login).apply()
    }

    /**
     * Получает логин пользователя
     */
    fun getUserLogin(): String {
        return prefs.getString(KEY_USER_LOGIN, "") ?: ""
    }

    /**
     * Сохраняет токен авторизации
     */
    fun saveUserToken(token: String) {
        prefs.edit().putString(KEY_USER_TOKEN, token).apply()
    }

    /**
     * Получает токен авторизации
     */
    fun getUserToken(): String {
        return prefs.getString(KEY_USER_TOKEN, "") ?: ""
    }

    /**
     * Включает/отключает уведомления
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    /**
     * Проверяет, включены ли уведомления
     */
    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    /**
     * Очищает данные пользователя при выходе из аккаунта
     */
    fun clearUserData() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_LOGIN)
            .remove(KEY_USER_TOKEN)
            .apply()
    }
}