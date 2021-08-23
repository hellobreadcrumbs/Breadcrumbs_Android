package com.breadcrumbsapp.model

import java.io.Serializable

data class GetRankingModel(
    var status: Boolean,
    var message: List<Message>
) {
    data class Message(
        var id: String,
        var username: String,
        var profile_picture: String,
        var experience: String,
        var total_duration: String,
        var total_exp: String,
        var total_completed: String
    ):Serializable
}