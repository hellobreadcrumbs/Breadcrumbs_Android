package com.breadcrumbsapp.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
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


internal class MyAchievementsListAdapter(getFeed: List<GetUserAchievementsModel.Message>) :
    RecyclerView.Adapter<MyAchievementsListAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<GetUserAchievementsModel.Message> = getFeed
    private lateinit var context: Context
    private var completedPOI: Int = 0
    private var isLocked:Boolean=false


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val trailName: TextView = view.findViewById(R.id.my_achievement_adapter_trail_name_tv)
        val trailIcon: ImageView = view.findViewById(R.id.my_achievement_adapter_icon_iv)
        val trailDescriptor: TextView =
            view.findViewById(R.id.my_achievement_adapter_trail_description)
        val completedPOICount: TextView = view.findViewById(R.id.my_achievement_adapter_poi_count)
        val progressBar: LinearProgressIndicator =
            view.findViewById(R.id.my_achievement_adapter_trail_progress_bar)
        val trailLayout:ConstraintLayout=view.findViewById(R.id.twilight_trail_layout)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_achievements_adapter, parent, false)
        context = parent.context
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.trailName.text = getFeedsLocalObj[position].title
        holder.trailDescriptor.text = getFeedsLocalObj[position].description
        val localImagePath =
            context.resources.getString(R.string.staging_url) + getFeedsLocalObj[position].badge_img
        Glide.with(context).load(localImagePath).into(holder.trailIcon)
        println("Adapter Size = ${getFeedsLocalObj.size}")

            if (getFeedsLocalObj[position].pois[position].uc_id != null) {
                ++completedPOI
            }
         


            if (getFeedsLocalObj[position].ua_id != null) {

                holder.completedPOICount.text =
                    "${getFeedsLocalObj[position].pois.size} / ${getFeedsLocalObj[position].pois.size}"

                holder.progressBar.background =
                    context.resources.getDrawable(R.drawable.achievement_progress_full_bar_bg)
                holder.progressBar.max = getFeedsLocalObj[position].pois.size

            } else {
                holder.completedPOICount.text =
                    "$completedPOI / ${getFeedsLocalObj[position].pois.size}"

                ObjectAnimator.ofInt(holder.progressBar, "progress", completedPOI)
                    .setDuration(100)
                    .start()
                if (getFeedsLocalObj[position].title == "Wildlife Warrior") {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.wildlife_warrior_locked_icon))
                        .into(holder.trailIcon)
                }
            }


        println("completedTrail = ${getFeedsLocalObj[position].title} :: ${getFeedsLocalObj[position].ua_id}")


        holder.itemView.setOnClickListener(View.OnClickListener {


                println("getFeedsLocalObj.indices")
                isLocked = getFeedsLocalObj[position].ua_id == null
                context.startActivity(
                    Intent(
                        context,
                        MyAchievementScreenDetailsActivity::class.java
                    ).putExtra("isLocked",isLocked).putExtra("userAchievementModelData", getFeedsLocalObj[position])
                )



        })




    }


    override fun getItemCount(): Int {
        return getFeedsLocalObj.size
    }
}