package com.breadcrumbsapp.model

data class GetUserRankingModel(

    var status: Boolean,
    var message: Message
) {
    data class Message(
        var id: String,
        var username: String,
        var profile_picture: String,
        var experience: String,
        var total_duration: String,
        var total_exp: String,
        var total_completed: String
    )
}
