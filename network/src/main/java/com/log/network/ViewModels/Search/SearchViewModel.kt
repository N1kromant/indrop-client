package com.log.network.ViewModels.Search

import android.os.Parcel
import android.os.Parcelable
import com.log.network.ViewModels.BaseMVI.BaseViewModel

class SearchViewModel() : BaseViewModel<SearchIntent>(), Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchViewModel> {
        override fun createFromParcel(parcel: Parcel): SearchViewModel {
            return SearchViewModel(parcel)
        }

        override fun newArray(size: Int): Array<SearchViewModel?> {
            return arrayOfNulls(size)
        }
    }

}