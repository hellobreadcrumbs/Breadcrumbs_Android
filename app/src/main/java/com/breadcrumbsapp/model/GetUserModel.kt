package com.breadcrumbsapp.model

data class GetUserModel(
    var status:Boolean,
    var message:Message
)
{
    data class Message(
        var id:String,
        var email:String,
        var profile_picture:String,
        var username:String,
        var password:String,
        var rank:String,
        var finished_pois:String,
        var finished_trails:String,
        var experience:String,
        var is_sponsor:String,
        var register_platform:String,
        var created:String,
        var updated:String
    )
}
