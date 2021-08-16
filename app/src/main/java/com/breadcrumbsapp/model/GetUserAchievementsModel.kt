package com.breadcrumbsapp.model

import java.io.Serializable

data class GetUserAchievementsModel(
    var status: Boolean,
    var message: List<Message>
) {
    data class Message
        (

        var id: String,
        var trail_id: String,
        var badge_img: String,
        var title: String,
        var description: String,
        var experience: String,
        var completed: String,
        var created: String,
        var updated: String,
        var type: String,
        var zone_1_title: String,
        var zone_1: String,
        var zone_2_title: String,
        var zone_2: String,
        var zone_3_title: String,
        var zone_3: String,
        var zone_4_title: String,
        var zone_4: String,
        var zone_5_title: String,
        var zone_5: String,
        var zone_6_title: String,
        var zone_6: String,
        var name: String,
        var t_desc: String,
        var icon: String,
        var ua_id: String,
        var username: String,
        var pois: List<Pois>


    ) :Serializable
    {
        data class Pois(
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
            var uc_id: String,
            var total_xp: String
        ):Serializable
    }
}