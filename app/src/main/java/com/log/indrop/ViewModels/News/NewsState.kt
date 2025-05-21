package com.log.indrop.ViewModels.News

import com.log.data.PostData
import com.log.network.ViewModels.BaseMVI.BaseState

data class NewsState (
    val posts: List<PostData> = emptyList<PostData>(),
    val postDraft: PostData? = null
) : BaseState
