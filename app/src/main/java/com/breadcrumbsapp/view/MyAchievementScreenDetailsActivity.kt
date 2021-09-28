package com.breadcrumbsapp.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.AchievementDetailsAdapter
import com.breadcrumbsapp.databinding.AchievementUnlockDetailsScreenLayoutBinding

import com.breadcrumbsapp.model.GetUserAchievementsModel
import com.breadcrumbsapp.util.CommonData
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.achievement_unlock_details_screen_layout.*

class MyAchievementScreenDetailsActivity : AppCompatActivity() {
    private lateinit var achievementDetailsAdapter: AchievementDetailsAdapter
    private lateinit var getUserAchievementsModel: GetUserAchievementsModel.Message
    private lateinit var binding: AchievementUnlockDetailsScreenLayoutBinding
    var completedPOI:Int=0
    private lateinit var trailIcon:String
    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon
    )

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AchievementUnlockDetailsScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLocked = intent.getBooleanExtra("isLocked", false)
        getUserAchievementsModel = intent.extras?.get("userAchievementModelData") as GetUserAchievementsModel.Message

        achievement_name.text = getUserAchievementsModel.title
        val badgeImage = resources.getString(R.string.staging_url) + getUserAchievementsModel.badge_img

        trailIcon = resources.getString(R.string.staging_url) + getUserAchievementsModel.icon
        println("Ach Det ::: ${getUserAchievementsModel.trail_id}")

        if(getUserAchievementsModel.trail_id=="4")
        {
            Glide.with(applicationContext).load(trailIcons[1]).into(achievement_details_trail_icon)
            Glide.with(applicationContext).load(trailIcons[1]).into(achievement_details_lock_trail_icon)
        }
        else if(getUserAchievementsModel.trail_id=="6")
        {
            Glide.with(applicationContext).load(trailIcons[2]).into(achievement_details_trail_icon)
            Glide.with(applicationContext).load(trailIcons[2]).into(achievement_details_lock_trail_icon)
        }

        unlock_screen_trail_name.text=getUserAchievementsModel.name
        lock_screen_trail_name.text=getUserAchievementsModel.name

        achievement_details_lock_info_icon.setOnClickListener {

            if (getUserAchievementsModel.trail_id == "4") {
                startActivity(
                    Intent(
                        applicationContext,
                        TrailsDetailsActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //   .putExtra("position", position)
                        .putExtra("trail_id", getUserAchievementsModel.trail_id)
                        .putExtra("getTrailsListData", CommonData.getTrailsData!![0])
                )
            } else if (getUserAchievementsModel.trail_id == "6") {
                startActivity(
                    Intent(
                        applicationContext,
                        TrailsDetailsActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //   .putExtra("position", position)
                        .putExtra("trail_id", getUserAchievementsModel.trail_id)
                        .putExtra("getTrailsListData", CommonData.getTrailsData!![1])
                )
            }
        }

        achievement_details_info_icon.setOnClickListener {

            if (getUserAchievementsModel.trail_id == "4") {
                startActivity(
                    Intent(
                        applicationContext,
                        TrailsDetailsActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //   .putExtra("position", position)
                        .putExtra("trail_id", getUserAchievementsModel.trail_id)
                        .putExtra("getTrailsListData", CommonData.getTrailsData!![0])
                )
            } else if (getUserAchievementsModel.trail_id == "6") {
                startActivity(
                    Intent(
                        applicationContext,
                        TrailsDetailsActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        //   .putExtra("position", position)
                        .putExtra("trail_id", getUserAchievementsModel.trail_id)
                        .putExtra("getTrailsListData", CommonData.getTrailsData!![1])
                )
            }

        }

        println("isLocked=> $isLocked")
        if (isLocked) {

            achievement_details_lock_layout.visibility = View.VISIBLE
            achievement_details_unlock_layout.visibility = View.GONE
            achievement_details_image.alpha=0.5f
            achievement_details_lock_iv.visibility=View.VISIBLE

            Glide.with(applicationContext).load(badgeImage).into(achievement_details_image)

           for(i in getUserAchievementsModel.pois.indices)
           {
               if(getUserAchievementsModel.pois[i].uc_id!=null)
               {
                   ++completedPOI
               }
           }

            achievement_details_lock_progress_bar.max = getUserAchievementsModel.pois.size
            ObjectAnimator.ofInt(achievement_details_lock_progress_bar, "progress", completedPOI)
                .setDuration(100)
                .start()

            println("experience::: ${getUserAchievementsModel.experience}")
            achievement_unlock_xp_points.text = "${getUserAchievementsModel.experience} XP\nPOINTS"
            println("experience::: ${achievement_unlock_xp_points.text}")

            non_completed_pois_rv.layoutManager =
                LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            achievementDetailsAdapter = AchievementDetailsAdapter(getUserAchievementsModel.pois)
            non_completed_pois_rv.adapter = achievementDetailsAdapter

             progress_count.text="$completedPOI/${getUserAchievementsModel.pois.size}"

        } else {
            achievement_details_lock_layout.visibility = View.GONE
            achievement_details_unlock_layout.visibility = View.VISIBLE
            achievement_details_image.alpha=1.0f
            achievement_details_lock_iv.visibility=View.GONE

            Glide.with(applicationContext).load(badgeImage).into(achievement_details_image)
            for(i in getUserAchievementsModel.pois.indices)
            {
                if(getUserAchievementsModel.pois[i].uc_id!=null)
                {
                    ++completedPOI
                }
            }

            achievement_details_unlock_progress_bar.max = getUserAchievementsModel.pois.size
            ObjectAnimator.ofInt(achievement_details_unlock_progress_bar, "progress", completedPOI)
                .setDuration(100)
                .start()

            println("experience::: ${getUserAchievementsModel.experience}")
            achievements_details_xp_points.text =
                "${getUserAchievementsModel.experience} XP\nPOINTS"
            println("experience::: ${achievements_details_xp_points.text}")


            completed_pois_rv.layoutManager =
                LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            achievementDetailsAdapter = AchievementDetailsAdapter(getUserAchievementsModel.pois)
            completed_pois_rv.adapter = achievementDetailsAdapter

            unlock_progress_count.text="$completedPOI/${getUserAchievementsModel.pois.size} COMPLETED!"

        }

        achievements_unlock_screen_backButton.setOnClickListener {
            finish()
        }

    }
}