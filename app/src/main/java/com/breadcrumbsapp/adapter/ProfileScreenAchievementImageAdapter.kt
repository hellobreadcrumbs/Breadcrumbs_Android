package com.breadcrumbsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetUserAchievementsModel
import com.bumptech.glide.Glide


internal class ProfileScreenAchievementImageAdapter(getFeed: List<GetUserAchievementsModel.Message>) :
    RecyclerView.Adapter<ProfileScreenAchievementImageAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<GetUserAchievementsModel.Message> = getFeed
    private lateinit var context: Context


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var achievementImage: ImageView = view.findViewById(R.id.achievement_adapter_image)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_screen_achievement_image_arrangment_adapter, parent, false)
        context = parent.context
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        println("getFeedsLocalObj ${getFeedsLocalObj.size}")
        println("getFeedsLocalObj ${getFeedsLocalObj[position].badge_img}")
        println("getFeedsLocalObj ${getFeedsLocalObj[position].title}")

        val localImagePath =
            context.resources.getString(R.string.staging_url) + getFeedsLocalObj[position].badge_img
        println("getFeedsLocalObj $localImagePath")
        Glide.with(context).load(localImagePath).into(holder.achievementImage)


        if (getFeedsLocalObj.size<5)
        {

        }

    }


    override fun getItemCount(): Int {
        return 5
    }
}