package com.breadcrumbsapp.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetUserAchievementsModel
import com.breadcrumbsapp.view.MyAchievementScreenDetailsActivity
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator


internal class AchievementDetailsAdapter(getFeed: List<GetUserAchievementsModel.Message.Pois>) :
    RecyclerView.Adapter<AchievementDetailsAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<GetUserAchievementsModel.Message.Pois> = getFeed
    private lateinit var context: Context



    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val trailName: TextView = view.findViewById(R.id.achievement_poi_completed)


    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.achievement_details_adapter, parent, false)
        context = parent.context
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.trailName.text = getFeedsLocalObj[position].title

        if(getFeedsLocalObj[position].uc_id!=null)
        {
            holder.trailName.text=Html.fromHtml("<strike>${getFeedsLocalObj[position].title}</strike>")
        }


    }


    override fun getItemCount(): Int {
        return getFeedsLocalObj.size
    }
}