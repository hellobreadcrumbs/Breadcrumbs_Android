package com.breadcrumbsapp.util

import com.breadcrumbsapp.model.*
import java.util.ArrayList


class CommonData {

    companion object {
        val eventsList = ArrayList<GetEventsModel.Message>()
        val feedList = ArrayList<GetFeedDataModel.Message>()
        val commonUserRankList=ArrayList<GetRankingModel.Message>()
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



        private fun calculateUserLevel(exp: Int):Int {
            var ranking: String = ""
            var level: Int = 0
            var base: Int = 0
            var nextLevel: Int = 0
            when (exp) {
                in 0..999 -> { // 1000 thresh
                    ranking = "Recruit"
                    level = 1
                    base = 1000
                    nextLevel = 1000
                }
                in 1000..1999 -> { // 1000 thresh
                    ranking = "Recruit"
                    level = 2
                    base = 1000
                    nextLevel = 2000
                }
                in 2000..2999 -> { // 1000 thresh
                    ranking = "Recruit"
                    level = 3
                    base = 2000
                    nextLevel = 3000
                }
                in 3000..3999 -> { // 1000 thresh
                    ranking = "Recruit"
                    level = 4
                    base = 3000
                    nextLevel = 4000
                }
                in 4000..5999 -> { // 2000 thresh
                    ranking = "Recruit"
                    level = 5
                    base = 4000
                    nextLevel = 6000
                }
                in 6000..7999 -> { // 2000 thresh
                    ranking = "Recruit"
                    level = 6
                    base = 6000
                    nextLevel = 8000
                }
                in 8000..9999 -> { // 2000 thresh
                    ranking = "Recruit"
                    level = 7
                    base = 8000
                    nextLevel = 10000
                }
                in 10000..11999 -> { // 2000 thresh
                    ranking = "Recruit"
                    level = 8
                    base = 10000
                    nextLevel = 12000
                }
                in 12000..13999 -> { // 2000 thresh
                    ranking = "Recruit"
                    level = 9
                    base = 12000
                    nextLevel = 14000
                }
                in 14000..16999 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 10
                    base = 14000
                    nextLevel = 17000

                }
                in 17000..20499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 11
                    base = 17000
                    nextLevel = 20500

                }
                in 20500..24499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 12
                    base = 20500
                    nextLevel = 24500

                }
                in 24500..28499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 13
                    base = 24500
                    nextLevel = 28500

                }
                in 28500..33499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 14
                    base = 28500
                    nextLevel = 33500

                }
                in 33500..38999 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 15
                    base = 33500
                    nextLevel = 39000

                }
                in 39000..44999 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 16
                    base = 39000
                    nextLevel = 45000

                }
                in 45000..51499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 17
                    base = 45000
                    nextLevel = 51500

                }
                in 51500..58499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 18
                    base = 51500
                    nextLevel = 58500

                }
                in 58500..65999 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 19
                    base = 58500
                    nextLevel = 66000

                }
                in 66000..73999 -> { // 2000 thresh
                    ranking = "Captain"
                    level = 20
                    base = 66000
                    nextLevel = 74000

                }
            }


        return 1
        }

    }
}