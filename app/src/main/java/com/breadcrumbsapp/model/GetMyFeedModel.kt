package com.breadcrumbsapp.model

data class GetMyFeedModel(
    var status:Boolean,
    var message : List<Message>
)
{
    data class Message(

        var username:String,
        var profile_picture:String,
        var like_count:String,
        var created:String,
        var photo_url:String,
        var description:String,
        var is_user_generated:String,
        var map_icon_dt_url:String,
        var title:String,
        var name:String
    )
}