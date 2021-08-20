package com.breadcrumbsapp.model

data class GetFriendsListModel(
    var status: Boolean,
    var message: List<Message>
) {
    data class Message
        (
        var id: String,
        var email : String,
        var profile_picture: String,
        var username : String,
        var password : String,
        var rank : String,
        var finished_pois: String,
        var finished_trails : String,
        var experience : String,
        var is_sponsor: String,
        var register_platform: String,
        var created : String,
        var updated : String,
        var uf_id: String,
        var user_id: String,
        var friend_id: String,
        var status : String,
        var u_username: String,
        var u_rank: String,
        var u_profile_picture: String,
        var u_experience: String
    )
}
