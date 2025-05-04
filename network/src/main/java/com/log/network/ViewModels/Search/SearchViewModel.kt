package com.log.network.ViewModels.Search

import android.os.Parcel
import android.os.Parcelable
import com.log.network.ViewModels.BaseMVI.BaseViewModel

class SearchViewModel() : BaseViewModel<SearchIntent, SearchState, SearchEffect>(SearchState()) {
    override suspend fun handleIntent(intent: SearchIntent) {
        TODO("Not yet implemented")
    }

}