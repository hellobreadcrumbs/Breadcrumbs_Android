package com.breadcrumbsapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.TrailDetailsLayoutBinding
import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.view.rewards.GetRewardsDataModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.trail_details_layout.*

class TrailsDetailsActivity:AppCompatActivity()
{
    private var trailBanner = intArrayOf(
        R.drawable.pioneer_trail_banner,
        R.drawable.pioneer_trail_banner,
        R.drawable.anthology_trail_banner

    )

    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon

    )

    private var trailNameString: Array<String> = arrayOf("PIONEER TRAIL","WILD ABOUT TWILIGHT TRAIL","ANTHOLOGY TRAIL")

    private lateinit var getTrailsModelList:GetTrailsModel.Message
    private lateinit var binding:TrailDetailsLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= TrailDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var positionInt:Int=intent.getIntExtra("position",0)
        getTrailsModelList = intent.extras?.get("getTrailsListData") as GetTrailsModel.Message
        println("positionStr : $positionInt")


   /*     Glide.with(applicationContext).load(trailBanner[positionInt]).into(trail_details_image)
        Glide.with(applicationContext).load(trailIcons[positionInt]).into(trail_details_trailIcon)
        tv_trail_name_banner.text=trailNameString[positionInt]  */


        // Live Data....
        var localImageUri =
            resources.getString(R.string.staging_url) + getTrailsModelList.banner_url
        Glide.with(applicationContext).load(localImageUri).into(trail_details_image)
        Glide.with(applicationContext).load(trailIcons[1]).into(trail_details_trailIcon)
        tv_trail_name_banner.text=getTrailsModelList.name
        trail_details_about_content.text=getTrailsModelList.description
        var localImageUriCreatorPost =
            resources.getString(R.string.live_url) + getTrailsModelList.profile_picture
        Glide.with(applicationContext).load(localImageUriCreatorPost).into(iv_trail_details_creator_post)
        tv_trail_by_content.text=getTrailsModelList.username


        iv_open_leaderboard.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,LeaderBoardActivity::class.java))
        })

        trail_details_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        iv_open_creator_post.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,CreatorPostActivity::class.java))
        })





    }



}