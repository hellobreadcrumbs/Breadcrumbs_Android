package com.breadcrumbsapp.model

import java.io.Serializable

data class BeginChallengeModel(
    var status: Boolean,
    var message: Message
) {
    data class Message(

        var achievement: String,
        var completed_trail: Boolean

    ) : Serializable
}
