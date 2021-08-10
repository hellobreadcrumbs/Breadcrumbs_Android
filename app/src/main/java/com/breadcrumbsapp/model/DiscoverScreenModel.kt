package com.breadcrumbsapp.model


data class DiscoverScreenModel(

    var status: Boolean,
    var message: List<Message>
)


{
    data class Message(


        var id: String,
        var user_id: String,
        var client_companies_id: String,
        var name: String,
        var banner_url: String,
        var description: String,
        var startdate: String,
        var enddate: String,
        var map_icon_url: String,
        var map_icon_clear_url: String,
        var map_icon_dt_url: String,
        var map_icon_dt_clear_url: String,
        var map_icon_ar_url: String,
        var map_icon_ar_clear_url: String,
        var map_icon_ar_dt_url: String,
        var map_icon_ar_dt_clear_url: String,
        var grid_icon_url: String,
        var polygon: String,
        var map_style: String,
        var banner_bg: String,
        var text_colors: String,
        var direct_bearing: String,
        var created: String,
        var updated: String,
        var poi_count: String,
        var completed_poi_count: String,
        var profile_picture: String,
        var username: String,
        var uid: String,
        var markers: List<Markers>


    ){
        data class Markers(

            var id: String,
            var trail_id: String,
            var resource_index: String,
            var name: String,
            var latitude: String,
            var longitude: String

        )
    }
}