package com.breadcrumbsapp.model

import java.io.Serializable

data class RecommendedFriendsModel(
    var status: Boolean,
    var message: List<Message>
) {
    data class Message(
        var ua_id: String,
        var ua_status: String,
        var id: String,
        var email: String,
        var profile_picture: String,
        var username: String,
        var password: String,
        var rank: String,
        var finished_pois: String,
        var finished_trails: String,
        var experience: String,
        var is_sponsor: String,
        var register_platform: String,
        var created: String,
        var updated: String
    ) : Serializable
}
