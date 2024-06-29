package com.log.network.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.log.data.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkViewModel: ViewModel() {
//    public val newInputMessage = MutableLiveData<Message>()
//    public val newOutputMessage = MutableLiveData<String>()
    private val _newOutputMessage = MutableStateFlow<String?>(null)
    var newOutputMessage = _newOutputMessage.asStateFlow()

    fun newOutputMessage(message: String) {
        _newOutputMessage.value = message
    }
}