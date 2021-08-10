package com.breadcrumbsapp.util

import com.breadcrumbsapp.model.GetEventsModel
import com.breadcrumbsapp.model.GetFeedDataModel
import com.breadcrumbsapp.model.GetRankingModel
import com.breadcrumbsapp.model.GetUserModel


class CommonData {

    companion object {
        var eventsModelMessage: List<GetEventsModel.Message>? = null
        var getUserDetails:GetUserModel.Message?=null
        var getFeedData:List<GetFeedDataModel.Message>?=null
        var getRankData:List<GetRankingModel.Message>?=null


        // leaderboards.js
  // line  =const level_info = getUserDisplayLevel(item.experience);

        /*fun calculateRanking(expStr : String) : String
        {
            var ranking = "Recruit"
            var level = 1
            var base = 0
            var nextLevel = 1000


            val exp=expStr.toInt()
            when (exp) {
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
                    //    badge = badge2
                }
                in 17000..20499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 11
                    base = 17000
                    nextLevel = 20500
                    //    badge = badge2
                }
                in 20500..24499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 12
                    base = 20500
                    nextLevel = 24500
                    //    badge = badge2
                }
                in 24500..28499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 13
                    base = 24500
                    nextLevel = 28500
                    //   badge = badge2
                }
                in 28500..33499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 14
                    base = 28500
                    nextLevel = 33500
                    //   badge = badge2
                }
                in 33500..38999 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 15
                    base = 33500
                    nextLevel = 39000
                    //   badge = badge2
                }
                in 39000..44999 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 16
                    base = 39000
                    nextLevel = 45000
                    //   badge = badge2
                }
                in 45000..51499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 17
                    base = 45000
                    nextLevel = 51500
                   // badge = badge2
                }
                in 51500..58499 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 18
                    base = 51500
                    nextLevel = 58500
                    // badge = badge2
                }
                in 58500..65999 -> { // 2000 thresh
                    ranking = "Navigator"
                    level = 19
                    base = 58500
                    nextLevel = 66000
                    // badge = badge2
                }
                in 66000..73999 -> { // 2000 thresh
                    ranking = "Captain"
                    level = 20
                    base = 66000
                    nextLevel = 74000
                    //   badge = badge3
                }
            }

            val percent = (exp - base) / (nextLevel - base) * 100
            val expToLevel = nextLevel - base - (exp - base)

            return
        }
*/
    }
}