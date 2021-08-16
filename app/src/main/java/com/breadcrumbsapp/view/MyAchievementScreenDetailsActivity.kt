package com.breadcrumbsapp.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.AchievementUnlockDetailsScreenLayoutBinding
import com.breadcrumbsapp.model.GetUserAchievementsModel
import com.breadcrumbsapp.util.CommonData
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.achievement_unlock_details_screen_layout.*
import kotlinx.android.synthetic.main.selfie_challenge_level_layout.*

class MyAchievementScreenDetailsActivity : AppCompatActivity() {
    private lateinit var getUserAchievementsModel: GetUserAchievementsModel.Message
    private lateinit var binding: AchievementUnlockDetailsScreenLayoutBinding
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AchievementUnlockDetailsScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var isLocked =intent.getBooleanExtra("isLocked",false)
        getUserAchievementsModel=intent.extras?.get("userAchievementModelData") as GetUserAchievementsModel.Message

        achievement_name.text=getUserAchievementsModel.title

        println("isLocked $isLocked")
        if(isLocked)
        {

            Glide.with(applicationContext).load(R.drawable.wildlife_warrior_locked_icon).into(achievement_image)

            ObjectAnimator.ofInt(achievement_details_lock_progress_bar, "progress", 78)
                .setDuration(100)
                .start()
        }
        else
        {
            Glide.with(applicationContext).load(R.drawable.achievement_details_aardvark_icon).into(achievement_image)

            achievement_details_lock_progress_bar.max=100
            ObjectAnimator.ofInt(achievement_details_lock_progress_bar, "progress", 100)
                .setDuration(100)
                .start()

            for (i in CommonData.getUserAchievementsModel!!.indices) {


                if (CommonData.getUserAchievementsModel!![i].ua_id != null) {
                    completed_field_two.text = (Html.fromHtml("<strike>${CommonData.getUserAchievementsModel!![i].zone_1_title}</strike>"))
                    completed_field_three.text =
                        (Html.fromHtml("<strike>${CommonData.getUserAchievementsModel!![i].zone_2_title}</strike>"))
                    completed_field_four.text =
                        (Html.fromHtml("<strike>${CommonData.getUserAchievementsModel!![i].zone_3_title}</strike>"))
                    completed_field_five.text =
                        (Html.fromHtml("<strike>${CommonData.getUserAchievementsModel!![i].zone_4_title}</strike>"))
                    completed_field_six.text = (Html.fromHtml("<strike>${CommonData.getUserAchievementsModel!![i].zone_5_title}</strike>"))
                }
            }

        }




        achievement_details_info_icon.setOnClickListener(View.OnClickListener {
            try {
                println("Size::: ${CommonData.getTrailsData!!.size}")

                startActivity(
                    Intent(
                        applicationContext,
                        TrailsDetailsActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("position", 0)
                        .putExtra("getTrailsListData", CommonData.getTrailsData!![0])
                )
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }

        })


        achievements_unlock_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}