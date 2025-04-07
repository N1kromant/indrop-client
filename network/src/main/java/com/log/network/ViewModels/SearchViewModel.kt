package com.log.network.ViewModels

import androidx.lifecycle.ViewModel
import com.log.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel: ViewModel() {
    private val _recentUsers = MutableStateFlow<UserData?>(null)
    val recentUsers = _recentUsers.asStateFlow()

    private val _newOutputMessage = MutableStateFlow<String?>(null)
    val newOutputMessage = _newOutputMessage.asStateFlow()

    fun newOutputMessage(message: String) {
        _newOutputMessage.value = message
    }
}