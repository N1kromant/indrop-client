package com.log.indrop.ViewModels.News

import com.log.data.PostData
import com.log.network.ViewModels.BaseMVI.BaseEffect

sealed class NewsEffect : BaseEffect {
//    data class newPost(val post: PostData): NewsEffect()
    data class RouteProfileEffect(val userid: Long) : NewsEffect()
}