package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetRankingModel
import java.lang.Integer.parseInt
import kotlin.math.floor

internal class LeaderBoardPlayerListAdapter(
    getRankData: List<GetRankingModel.Message>
) :
    RecyclerView.Adapter<LeaderBoardPlayerListAdapter.MyViewHolder>() {

    private var getRankDataObj: List<GetRankingModel.Message> = getRankData

    private var context: Context? = null

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var playerName: TextView = view.findViewById(R.id.leaderBoard_adapter_playerName)
        var totalXPTextView: TextView = view.findViewById(R.id.totalXP)
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
        holder.totalXPTextView.text = getRankDataObj[position].experience + " XP"


        var display= "0H0M";
        if(getRankDataObj[position].total_duration != null) {
            var delta = parseInt(getRankDataObj[position].total_duration)
            var days = floor((delta / 86400).toDouble())
            if(days > 0) {
                delta -= days.toInt() * 86400
                // calculate (and subtract) whole hours
                var hours = floor((delta / 3600).toDouble()) % 24
                delta -= hours.toInt() * 3600;

                //display = days+"D"+hours+"H";
                display="$days +D+ $hours +H"
            } else {
                var hours = floor((delta / 3600).toDouble()) % 24
                delta -= hours.toInt()  * 3600

                // calculate (and subtract) whole minutes
                var minutes = floor((delta / 60).toDouble()) % 60
                delta -= minutes.toInt()  * 60

               // display = hours+"H"+minutes+"M";
                display = "$hours +H+ $minutes +M";
            }
        }



    }

    override fun getItemCount(): Int {
        return getRankDataObj.size
    }
}