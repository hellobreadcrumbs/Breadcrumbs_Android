package com.breadcrumbsapp.model

data class GetFeedDataModel(
    var status:Boolean,
    var message : List<Message>
)
{
    data class Message(
        var user_id:String,
        var username:String,
        var profile_picture:String,
        var f_id:String,
        var like_count:String,
        var created:String,
        var photo_url:String,
        var description:String,
        var is_user_generated:String,
        var map_icon_dt_url:String,
        var title:String,
        var name:String,
        var ul_id:String
    )
}