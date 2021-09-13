package com.breadcrumbsapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetRankingModel
import com.breadcrumbsapp.view.profile.FriendProfileScreenActivity
import java.lang.Integer.parseInt

internal class LeaderBoardPlayerListAdapter(
    getRankData: List<GetRankingModel.Message>
) :
    RecyclerView.Adapter<LeaderBoardPlayerListAdapter.MyViewHolder>() {

    private var getRankDataObj: List<GetRankingModel.Message> = getRankData

    private var context: Context? = null

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var playerName: TextView = view.findViewById(R.id.leaderBoard_adapter_playerName)
        var totalXPTextView: TextView = view.findViewById(R.id.totalXP)
        var rankTV: TextView = view.findViewById(R.id.leaderboard_adapter_rank_tv)
        var totalDurationTV: TextView = view.findViewById(R.id.adapter_total_duration)
        var completedPOITV: TextView = view.findViewById(R.id.adapter_completed_POIs)
        var adapterMainLayout: ConstraintLayout =
            view.findViewById(R.id.adapter_userInformationLayout)
        var playerLevel: TextView = view.findViewById(R.id.leaderBoard_adapter_player_level)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_list_adapter, parent, false)
        context = parent.context
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.playerName.text = getRankDataObj[position].username
        holder.totalXPTextView.text = "${getRankDataObj[position].total_exp} XP"
        holder.rankTV.text = "# ${position + 1}"
        holder.completedPOITV.text = "${getRankDataObj[position].total_completed}/17 POIs"

        try {
            if(getRankDataObj[position].total_exp!=null)
            {
                val expIntVal: Int = parseInt(getRankDataObj[position].total_exp)
                holder.playerLevel.text = calculateUserLevel(expIntVal)
            }
            else{
                val expIntVal: Int = 250
                holder.playerLevel.text = calculateUserLevel(expIntVal)
            }

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }


        when (position) {
            0 -> {
                holder.adapterMainLayout.setBackgroundResource(R.drawable.leaderboard_rank_1)
                holder.rankTV.setTextColor(Color.parseColor("#DF7103"))
            }
            1 -> {
                holder.adapterMainLayout.setBackgroundResource(R.drawable.leaderboard_rank_2)
                holder.rankTV.setTextColor(Color.parseColor("#AFAFAF"))
            }
            2 -> {
                holder.adapterMainLayout.setBackgroundResource(R.drawable.leaderboard_rank_3)
                holder.rankTV.setTextColor(Color.parseColor("#AA5419"))
            }
            else -> {
                holder.adapterMainLayout.setBackgroundResource(R.drawable.leaderboard_rank_4_and_above)
                holder.rankTV.setTextColor(Color.parseColor("#93BCAC"))
            }
        }

        if(getRankDataObj[position].total_duration!=null)
        {
            var display = "0H 0M"
            val integerValue: Int = parseInt(getRankDataObj[position].total_duration)
            val hours = integerValue / 3600
            val temp = integerValue - hours * 3600
            val minutes = temp / 60
            display = "${hours}H ${minutes}M"
            holder.totalDurationTV.text = display
        }
        else
        {
            var display = "0H 0M"
            val integerValue: Int = 15000
            val hours = integerValue / 3600
            val temp = integerValue - hours * 3600
            val minutes = temp / 60
            display = "${hours}H ${minutes}M"
            holder.totalDurationTV.text = display
        }


        holder.itemView.setOnClickListener {
            context!!.startActivity(
                Intent(context, FriendProfileScreenActivity::class.java)
                    .putExtra("username", getRankDataObj[position].username)
                    .putExtra("friend_id", getRankDataObj[position].id)
                    .putExtra("total_xp", getRankDataObj[position].total_exp)
                    .putExtra("profile_pic", getRankDataObj[position].profile_picture)
                    .putExtra("player_level",holder.playerLevel.text.toString())
            )
        }
    }

    override fun getItemCount(): Int {
        return getRankDataObj.size
    }


    private fun calculateUserLevel(exp: Int): String {
        var ranking: String = ""
        var level: Int = 0
        var base: Int = 0
        var nextLevel = 0
        when (exp) {
            in 0..999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 1
                base = 1000
                nextLevel = 2000
            }
            in 1000..1999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 2
                base = 1000
                nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 3
                base = 2000
                nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 4
                base = 3000
                nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 5
                base = 4000
                nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 6
                base = 6000
                nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 7
                base = 8000
                nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 8
                base = 10000
                nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 9
                base = 12000
                nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "NAVIGATOR"
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
        // levelTextView.text = "$ranking Lv. $level"
        return "$ranking Lv. $level"

/*        var expToLevel = (nextLevel - base) - (exp - base)

        println("expToLevel= $expToLevel")
        sharedPreference.saveSession("level_text_value", levelTextView.text.toString())
        sharedPreference.saveSession("expTo_level_value", expToLevel)
        sharedPreference.saveSession("xp_point_base_value", base)
        sharedPreference.saveSession("xp_point_nextLevel_value", nextLevel)
        sharedPreference.saveSession("lv_value", "LV ${level + 1}")*/

    }
}