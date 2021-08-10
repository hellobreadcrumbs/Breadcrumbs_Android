package com.breadcrumbsapp.model

data class GetEventsModel(

    var status : Boolean,
     var message : List<Message>

)
{
    data class Message(
        var id: String,
        var trail_id: String,
        var ar_id: String,
        var title: String,
        var description: String,
        var hint: String,
        var poi_img: String,
        var qr_code: String,
        var experience: String,
        var latitude: String,
        var longitude: String,
        var zone: String,
        var created: String,
        var updated: String,
        var trail_name: String,
        var map_icon_url: String,
        var map_icon_clear_url: String,
        var map_icon_dt_url: String,
        var map_icon_dt_clear_url: String,
        var map_icon_ar_url: String,
        var map_icon_ar_clear_url: String,
        var map_icon_ar_dt_url: String,
        var map_icon_ar_dt_clear_url: String,
        var direct_bearing: String,
        var text_colors: String,
        var ch_type: String,
        var ch_question: String,
        var ch_image: String,
        var ch_selections: String,
        var ch_answer: String,
        var ch_set_answer: String,
        var ch_hints: String,
        var ch_experience: String,
        var ch_trivia: String,
        var uc_last_visit: String,
        var uc_exp: String,
        var uc_extra_exp: String,
        var uc_answer: String,
        var uc_img: String,
        var disc_id: String
    )

}
