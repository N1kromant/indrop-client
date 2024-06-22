package com.log.network.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.log.data.Message

class NetworkViewModel: ViewModel() {
    public val newInputMessage = MutableLiveData<Message>()
    public val newOutputMessage = MutableLiveData<String>()
}