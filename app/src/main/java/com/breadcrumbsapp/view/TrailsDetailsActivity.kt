package com.breadcrumbsapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.TrailDetailsLayoutBinding

import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.trail_details_layout.*
import kotlinx.android.synthetic.main.user_profile_screen_layout.*

class TrailsDetailsActivity:AppCompatActivity()
{

    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon

    )

    private lateinit var getTrailsModelList:GetTrailsModel.Message
    private lateinit var binding: TrailDetailsLayoutBinding
    private lateinit var sessionHandlerClass:SessionHandlerClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= TrailDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionHandlerClass= SessionHandlerClass(applicationContext)

        val trailID=intent.extras?.get("trail_id") as String
        getTrailsModelList = intent.extras?.get("getTrailsListData") as GetTrailsModel.Message


        for(i in CommonData.getTrailsData!!.indices)
        {
            if(CommonData.getTrailsData!![i].id==trailID)
            {
                println("Details IF ::: Trail ID = ${CommonData.getTrailsData!![i].id} Completed_POI == ${CommonData.getTrailsData!![i].completed_poi_count}")

                tv_no_of_pois_report.text="${CommonData.getTrailsData!![i].completed_poi_count} /" +
                        " ${CommonData.getTrailsData!![i].poi_count} POIs DISCOVERED"
            }

        }


        if(getTrailsModelList.id=="4")
        {
            Glide.with(applicationContext).load(trailIcons[1]).into(trail_details_trailIcon)
            Glide.with(applicationContext).load(trailIcons[1]).into(iv_trail_details_creator_post)


        }
        if(getTrailsModelList.id=="6")
        {
            Glide.with(applicationContext).load(trailIcons[2]).into(trail_details_trailIcon)
            Glide.with(applicationContext).load(trailIcons[2]).into(iv_trail_details_creator_post)

        }

        post_creator_name.text=getTrailsModelList.username
        tv_trail_by_content.text=getTrailsModelList.username

        val posterImage=resources.getString(R.string.staging_url)+getTrailsModelList.profile_picture
        Glide.with(applicationContext).load(posterImage).into(iv_trail_details_creator_post)


        // Live Data....
        var localImageUri =
            resources.getString(R.string.staging_url) + getTrailsModelList.banner_url
        println("localImageUri $localImageUri")
        Glide.with(applicationContext).load(localImageUri).into(trail_details_image)

        tv_trail_name_banner.text=getTrailsModelList.name
        trail_details_about_content.text=getTrailsModelList.description

        iv_open_leaderboard.setOnClickListener {
            startActivity(Intent(applicationContext, LeaderBoardActivity::class.java))
        }

        trail_details_backButton.setOnClickListener {
            finish()
        }

        iv_open_creator_post.setOnClickListener {
            startActivity(
                Intent(applicationContext, CreatorPostActivity::class.java)
                    .putExtra("getTrailsListData", getTrailsModelList)
            )
        }


    }



}