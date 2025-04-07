package com.log.network.ViewModels.BaseMVI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Базовая абстрактная ViewModel для MVI паттерна
 *
 * @param S класс состояния, реализующий MviState
 * @param I класс intent, реализующий MviIntent
 * @param E класс effect, реализующий MviEffect
 * @param initialState начальное состояние
 */
abstract class BaseViewModel<I : BaseIntent, S : BaseState, E : BaseEffect>(initialState: S) : ViewModel() {

    // StateFlow для хранения текущего состояния
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // Flow для одноразовых событий (side effects)
    private val _effect = MutableStateFlow<E?>(null)
    val effect: StateFlow<E?> = _effect.asStateFlow()

    /**
     * Обрабатывает intent и обновляет состояние и/или генерирует эффекты
     * @param intent полученный intent для обработки
     */
    fun processIntent(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    /**
     * Абстрактный метод для обработки intent
     * @param intent intent для обработки
     */
    protected abstract suspend fun handleIntent(intent: I)

    /**
     * Обновляет текущее состояние
     * @param reduce лямбда, которая получает текущее состояние и возвращает новое
     */
    protected fun updateState(reduce: (S) -> S) {
        _state.update(reduce)
    }

    /**
     * Отправляет одноразовый эффект
     * @param effect эффект для отправки
     */
    protected fun emitEffect(effect: E) {
        _effect.value = effect
        // Сбрасываем эффект после эмиссии для одноразового использования
        _effect.value = null
    }
}

