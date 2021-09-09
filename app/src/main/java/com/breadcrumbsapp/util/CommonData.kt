package com.breadcrumbsapp.util

import com.breadcrumbsapp.model.*


class CommonData {

    companion object {
        val eventsList = java.util.ArrayList<GetEventsModel.Message>()
        val feedList = java.util.ArrayList<GetFeedDataModel.Message>()
        var eventsModelMessage: List<GetEventsModel.Message>? = null
        var getUserDetails:GetUserModel.Message?=null
        var getFeedData:List<GetFeedDataModel.Message>?=null
        var getRankData:List<GetRankingModel.Message>?=null
        var getUserRankData: GetUserRankingModel.Message?=null
        var getTrailsData:List<GetTrailsModel.Message>?=null
        var getMyFeedData:List<GetMyFeedModel.Message>?=null
        var getRecommendedFriendsModel:List<RecommendedFriendsModel.Message>?=null
        var getFriendListModel:List<GetFriendsListModel.Message>?=null
        var getUserAchievementsModel:List<GetUserAchievementsModel.Message>?=null
        var getFriendAchievementsModel:List<GetUserAchievementsModel.Message>?=null
        var getBeginChallengeModel:BeginChallengeModel.Message?=null



    }
}