package com.breadcrumbsapp.view.rewards

import java.io.Serializable

data class GetRewardsDataModel(
    var status: Boolean,
    var message: List<Message>
) {
    data class Message(
        var id: String,
        var user_id: String,
        var reward_id: String,
        var redeem_status: String,
        var vendor_staff_id: String,
        var scan_date: String,
        var reward_img: String,
        var rewardtitle: String,
        var enddate: String,
        var qr: String,
        var details: String,
        var tnc: String,
        var redeem_msg_title: String,
        var redeem_msg_body: String,
        var redeem_msg_img: String,
        var rid: String,
        var survey_id: String
    ) : Serializable
}