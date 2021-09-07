package com.breadcrumbsapp.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.model.GetUserAchievementsModel
import com.breadcrumbsapp.view.MyAchievementScreenDetailsActivity
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator


internal class MyAchievementsListAdapter(
    getFeed: List<GetUserAchievementsModel.Message>,
    getTrailList: List<GetTrailsModel.Message>
) :
    RecyclerView.Adapter<MyAchievementsListAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<GetUserAchievementsModel.Message> = getFeed
    private var getTrailListObj: List<GetTrailsModel.Message> = getTrailList
    private lateinit var context: Context

    // private var completedPOI: Int = 0
    private var isLocked: Boolean = false


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val trailName: TextView = view.findViewById(R.id.my_achievement_adapter_trail_name_tv)
        val trailIcon: ImageView = view.findViewById(R.id.my_achievement_adapter_icon_iv)
        val trailDescriptor: TextView =
            view.findViewById(R.id.my_achievement_adapter_trail_description)
        val completedPOICount: TextView = view.findViewById(R.id.my_achievement_adapter_poi_count)
        val progressBar: LinearProgressIndicator =
            view.findViewById(R.id.my_achievement_adapter_trail_progress_bar)
        val trailLayout: ConstraintLayout = view.findViewById(R.id.twilight_trail_layout)
        val achievementListLockIv: ImageView = view.findViewById(R.id.achievement_list_lock_iv)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_achievements_adapter, parent, false)
        context = parent.context
        return MyViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.trailName.text = getFeedsLocalObj[position].title
        holder.trailDescriptor.text = getFeedsLocalObj[position].description
        val localImagePath =
            context.resources.getString(R.string.staging_url) + getFeedsLocalObj[position].badge_img
        Glide.with(context).load(localImagePath).into(holder.trailIcon)
        println("Adapter Size = ${getFeedsLocalObj[position].pois.size}")


        /*   println("Adapter uc_id = TOP=  ${getFeedsLocalObj[position].pois[position].uc_id}")
           if (getFeedsLocalObj[position].pois[position].uc_id != null) {
               println("Adapter uc_id = IF=  ${getFeedsLocalObj[position].pois[position].uc_id}")
               ++completedPOI
           }*/


        // ua_id !=null means, This achievement completed already.
        if (getFeedsLocalObj[position].ua_id != null) {
            var completedPOI: Int = 0
            holder.trailIcon.alpha = 1.0f
            holder.achievementListLockIv.visibility = View.GONE
            holder.completedPOICount.text =
                "${getTrailListObj[position].completed_poi_count} / ${getFeedsLocalObj[position].pois.size}"

            holder.progressBar.background = context.resources.getDrawable(R.drawable.achievement_progress_full_bar_bg)

            holder.progressBar.max = getFeedsLocalObj[position].pois.size


            println("Adapter Count = IF = ${getFeedsLocalObj[position].pois.size} / ${getFeedsLocalObj[position].pois.size}")

            for (i in getFeedsLocalObj[position].pois.indices) {
                if (getFeedsLocalObj[position].pois[i].uc_id != null) {
                    ++completedPOI
                }
            }

            holder.completedPOICount.text =
                "$completedPOI/ ${getFeedsLocalObj[position].pois.size}"

        } else if (getFeedsLocalObj[position].ua_id == null) {

            var completedPOI: Int = 0

            for (i in getFeedsLocalObj[position].pois.indices) {
                if (getFeedsLocalObj[position].pois[i].uc_id != null) {
                    ++completedPOI
                }
            }


            holder.completedPOICount.text =
                "$completedPOI / ${getFeedsLocalObj[position].pois.size}"

            println("POI Completion :: $position== ${getFeedsLocalObj[position].completed} / ${getFeedsLocalObj[position].pois.size}")
            holder.progressBar.max = getFeedsLocalObj[position].pois.size
            ObjectAnimator.ofInt(holder.progressBar, "progress", completedPOI)
                .setDuration(100)
                .start()

            holder.trailIcon.alpha = 0.5f
            holder.achievementListLockIv.visibility = View.VISIBLE


        }


        println("completedTrail = ${getFeedsLocalObj[position].title} :: ${getFeedsLocalObj[position].ua_id}")


        holder.itemView.setOnClickListener(View.OnClickListener {



            isLocked = getFeedsLocalObj[position].ua_id == null



            context.startActivity(
                Intent(
                    context,
                    MyAchievementScreenDetailsActivity::class.java
                ).putExtra("isLocked", isLocked)
                    .putExtra("userAchievementModelData", getFeedsLocalObj[position])
            )


        })


    }


    override fun getItemCount(): Int {
        return getFeedsLocalObj.size
    }
}