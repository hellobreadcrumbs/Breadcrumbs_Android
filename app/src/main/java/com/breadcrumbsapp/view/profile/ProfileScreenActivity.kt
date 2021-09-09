package com.breadcrumbsapp.view.profile

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.ProfileScreenAchievementImageAdapter
import com.breadcrumbsapp.adapter.UserProfileScreenPostAdapter
import com.breadcrumbsapp.databinding.UserProfileScreenLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.MyAchievementScreenActivity
import com.breadcrumbsapp.view.MyFriendsListScreenActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.user_profile_screen_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.buffer
import okio.source
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileScreenActivity : AppCompatActivity() {

    private lateinit var userProfileScreenPostAdapter: UserProfileScreenPostAdapter
    private lateinit var profileScreenAchievementImageAdapter: ProfileScreenAchievementImageAdapter
    private var interceptor = intercept()
    private lateinit var binding: UserProfileScreenLayoutBinding
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private var completedPoiCount: Int = 0
    private var completedTrailCount: Int = 0

    fun readJsonFromAssets(context: Context, filePath: String): String? {
        try {
            val source = context.assets.open(filePath).source().buffer()
            return source.readByteString().string(Charset.forName("utf-8"))

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserProfileScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass = SessionHandlerClass(applicationContext)
        profile_screen_user_post_list.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        getMyFeedPostDetails()
        getUserAchievementsAPI()
        setOnClickListeners()





        if (CommonData.getTrailsData != null) {

            for (i in CommonData.getTrailsData!!.indices) {
                println("Details IF completed_poi_count :::  ${CommonData.getTrailsData!![i].completed_poi_count}")

                completedPoiCount += CommonData.getTrailsData!![i].completed_poi_count.toInt()

                if (CommonData.getTrailsData!![i].id == "4") {
                    println("Poi Count:: ID -> 4 = ${CommonData.getTrailsData!![i].poi_count}")
                    if (CommonData.getTrailsData!![i].poi_count == CommonData.getTrailsData!![i].completed_poi_count) {
                        ++completedTrailCount
                    }
                } else if (CommonData.getTrailsData!![i].id == "6") {
                    println("Poi Count:: ID -> 6 = ${CommonData.getTrailsData!![i].poi_count}")
                    if (CommonData.getTrailsData!![i].poi_count == CommonData.getTrailsData!![i].completed_poi_count) {
                        ++completedTrailCount
                    }
                }


            }

            completed_poi_count.text = "$completedPoiCount"
            println("Details IF completedTrailCount Overall :::  $completedTrailCount")
            completed_trail_count.text = "$completedTrailCount"

        } else {

            getTrailDetails()
        }


    }

    private fun setOnClickListeners() {
        edit_iv.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    ProfileEditActivity::class.java
                ).putExtra("from", "activity")
            )
        })

        /* if (sessionHandlerClass.getSession("player_photo_url") != null && sessionHandlerClass.getSession(
                 "player_photo_url"
             ) != ""
         ) {*/

        val localProfilePic =
            resources.getString(R.string.staging_url) +sessionHandlerClass.getSession("player_photo_url")
        Glide.with(applicationContext).load(localProfilePic)
            .placeholder(R.drawable.no_image)
            .into(profile_edit_screen_profile_pic_iv)
        // }
        user_profile_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        my_friends_list_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, MyFriendsListScreenActivity::class.java))
        })

        achievementLayout.setOnClickListener(View.OnClickListener {


            startActivity(Intent(applicationContext, MyAchievementScreenActivity::class.java))


        })

        achievements_image_adapter.setOnClickListener(View.OnClickListener {

            startActivity(Intent(applicationContext, MyAchievementScreenActivity::class.java))
        })


    }

    override fun onResume() {
        super.onResume()
        try {
            val localProfilePic =
                resources.getString(R.string.staging_url) +sessionHandlerClass.getSession("player_photo_url")
            Glide.with(applicationContext).load(localProfilePic)
                .placeholder(R.drawable.no_image)
                .into(profile_edit_screen_profile_pic_iv)
        }
        catch (e:java.lang.Exception)
        {
            e.printStackTrace()
        }
    }
    private fun getMyFeedPostDetails() {
        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", sessionHandlerClass.getSession("login_id"))
            // jsonObject.put("user_id", "198")

            println("getFeedPostData Url = ${resources.getString(R.string.staging_url)}")
            println("getFeedPostData Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getMyFeedDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getMyFeedData = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getMyFeedData != null) {


                                userProfileScreenPostAdapter = UserProfileScreenPostAdapter(
                                    CommonData.getMyFeedData!!,
                                    sessionHandlerClass.getSession("login_id")
                                )
                                profile_screen_user_post_list.adapter = userProfileScreenPostAdapter



                            }

                        }


                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUserAchievementsAPI() {
        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


            val jsonObject = JSONObject()
            jsonObject.put("user_id", sessionHandlerClass.getSession("login_id"))


            println("getUserAchievementsAPI Url = ${resources.getString(R.string.staging_url)}")
            println("getUserAchievementsAPI Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getUserAchievements(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getUserAchievementsModel = response.body()?.message


                       /* var temp_complete_trail:Int=0
                        var temp_compelete_poi=0
                        for(i in CommonData.getUserAchievementsModel!!.indices)
                        {
                            val achievement = CommonData.getUserAchievementsModel!![i]
                            if(achievement.ua_id!=null)
                            {
                                ++temp_complete_trail
                            }

                            for(j in achievement.pois!!.indices)
                            {
                                val poiss=achievement.pois[j]
                                if(poiss.uc_id!=null)
                                {
                                    ++temp_compelete_poi
                                }

                            }
                            println("Temp Data ====> $temp_complete_trail <> $temp_compelete_poi")
                        }*/



                        runOnUiThread {

                            if (CommonData.getUserAchievementsModel != null) {

                                println("UserAchieve Data: ${CommonData.getUserAchievementsModel!!.size}")


                                if(sessionHandlerClass.getSession("player_experience_points")=="")
                                {
                                    profile_screen_progress_bar.max =  CommonData.getUserDetails!!.experience.toInt()
                                    ObjectAnimator.ofInt(
                                        profile_screen_progress_bar,
                                        "progress",
                                        0
                                    )
                                        .setDuration(1000)
                                        .start()
                                }
                                else
                                {
                                    profile_screen_user_name.text =
                                        sessionHandlerClass.getSession("player_name")

                                    profile_screen_user_level.text =
                                        sessionHandlerClass.getSession("level_text_value")
                                   /* profile_screen_xp_point_value.text =
                                        "${sessionHandlerClass.getSession("player_experience_points")} XP"*/

                                    val progressBarMaxValue =
                                        sessionHandlerClass.getIntegerSession("xp_point_nextLevel_value")
                                    var expToLevel =
                                        sessionHandlerClass.getIntegerSession("expTo_level_value")
                                    val completedPoints =
                                        sessionHandlerClass.getSession("player_experience_points")
                                    val levelValue = sessionHandlerClass.getSession("lv_value")

                                    //val expInt=expToLevel-50

                                   // xp_to_next_level.text = "$expToLevel XP TO $levelValue"


                                    if(sessionHandlerClass.getSession("balance_xp_string")=="")
                                    {
                                        xp_to_next_level.text = "$expToLevel XP TO $levelValue"
                                    }
                                    else{
                                        xp_to_next_level.text=sessionHandlerClass.getSession("balance_xp_string")
                                    }

                                    if(sessionHandlerClass.getIntegerSession("total_gained_xp")==0)
                                    {
                                        profile_screen_xp_point_value.text ="250 XP"
                                    }
                                    else
                                    {
                                        profile_screen_xp_point_value.text="${sessionHandlerClass.getIntegerSession("total_gained_xp")} XP"
                                    }



                                    profile_screen_progress_bar.max = progressBarMaxValue
                                    ObjectAnimator.ofInt(
                                        profile_screen_progress_bar,
                                        "progress",
                                        completedPoints!!.toInt()
                                    )
                                        .setDuration(1000)
                                        .start()



                                    achievements_image_adapter.layoutManager = LinearLayoutManager(
                                        applicationContext,
                                        RecyclerView.HORIZONTAL,
                                        false
                                    )
                                    profileScreenAchievementImageAdapter =
                                        ProfileScreenAchievementImageAdapter(CommonData.getUserAchievementsModel!!)
                                    achievements_image_adapter.adapter =
                                        profileScreenAchievementImageAdapter


                                    if (CommonData.getUserAchievementsModel!!.isNotEmpty()) {
                                       loadAchievements()

                                    }

                                }



                            }

                        }


                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadAchievements() {
        println("load::: ${CommonData.getUserAchievementsModel!!.size}")

        var completedPOI=0
        when (CommonData.getUserAchievementsModel!!.size) {
            1 -> {

                val badgeImg =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg).into(achievement_icon_one)

                achievement_icon_two.visibility=View.GONE
                achievement_icon_two_lock_iv.visibility=View.VISIBLE
                achievement_icon_three.visibility=View.GONE
                achievement_icon_three_lock_iv.visibility=View.VISIBLE
                achievement_icon_four.visibility=View.GONE
                achievement_icon_four_lock_iv.visibility=View.VISIBLE
                achievement_icon_five.visibility=View.GONE
                achievement_icon_five_lock_iv.visibility=View.VISIBLE

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![0].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                //if(CommonData.getUserAchievementsModel!![0].ua_id != null)
                if(completedPOI==CommonData.getUserAchievementsModel!![0].pois.size)
                {

                    achievement_icon_one.alpha=1.0f
                    achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_one.alpha=0.5f
                    achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }


            }
            2 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)


                achievement_icon_three.visibility=View.GONE
                achievement_icon_three_lock_iv.visibility=View.VISIBLE
                achievement_icon_four.visibility=View.GONE
                achievement_icon_four_lock_iv.visibility=View.VISIBLE
                achievement_icon_five.visibility=View.GONE
                achievement_icon_five_lock_iv.visibility=View.VISIBLE

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![0].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![0].pois.size}")

                //if(CommonData.getUserAchievementsModel!![0].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![0].pois.size)
                {

                    achievement_icon_one.alpha=1.0f
                    achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_one.alpha=0.5f
                    achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![1].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![1].pois.size}")

                //if(CommonData.getUserAchievementsModel!![1].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![1].pois.size)
                {

                    achievement_icon_two.alpha=1.0f
                    achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_two.alpha=0.5f
                    achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }
            }
            3 -> {


                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(achievement_icon_three)

                achievement_icon_four.visibility=View.GONE
                achievement_icon_four_lock_iv.visibility=View.VISIBLE
                achievement_icon_five.visibility=View.GONE
                achievement_icon_five_lock_iv.visibility=View.VISIBLE

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![0].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![0].pois.size}")

                //if(CommonData.getUserAchievementsModel!![0].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![0].pois.size)
                {

                    achievement_icon_one.alpha=1.0f
                    achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_one.alpha=0.5f
                    achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![1].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![1].pois.size}")

                //if(CommonData.getUserAchievementsModel!![1].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![1].pois.size)
                {

                    achievement_icon_two.alpha=1.0f
                    achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_two.alpha=0.5f
                    achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![2].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![2].pois.size}")

                //if(CommonData.getUserAchievementsModel!![2].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![2].pois.size)
                {

                    achievement_icon_three.alpha=1.0f
                    achievement_icon_three_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_three.alpha=0.5f
                    achievement_icon_three_lock_iv.visibility=View.VISIBLE
                }
            }
            4 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(achievement_icon_three)

                val badgeImg4 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![3].badge_img
                Glide.with(applicationContext).load(badgeImg4).into(achievement_icon_four)


                achievement_icon_five.visibility=View.GONE
                achievement_icon_five_lock_iv.visibility=View.VISIBLE


                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![0].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![0].pois.size}")

                //if(CommonData.getUserAchievementsModel!![0].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![0].pois.size)
                {

                    achievement_icon_one.alpha=1.0f
                    achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_one.alpha=0.5f
                    achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![1].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![1].pois.size}")

                //if(CommonData.getUserAchievementsModel!![1].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![1].pois.size)
                {

                    achievement_icon_two.alpha=1.0f
                    achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_two.alpha=0.5f
                    achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![2].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![2].pois.size}")

                //if(CommonData.getUserAchievementsModel!![2].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![2].pois.size)
                {

                    achievement_icon_three.alpha=1.0f
                    achievement_icon_three_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_three.alpha=0.5f
                    achievement_icon_three_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![3].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![3].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![3].pois.size}")

                //if(CommonData.getUserAchievementsModel!![3].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![3].pois.size)
                {

                    achievement_icon_four.alpha=1.0f
                    achievement_icon_four_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_four.alpha=0.5f
                    achievement_icon_four_lock_iv.visibility=View.VISIBLE
                }

            }
            5 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(achievement_icon_three)

                val badgeImg4 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![3].badge_img
                Glide.with(applicationContext).load(badgeImg4).into(achievement_icon_four)

                val badgeImg5 =
                    resources.getString(R.string.staging_url) + CommonData.getUserAchievementsModel!![4].badge_img
                Glide.with(applicationContext).load(badgeImg5).into(achievement_icon_five)


                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![0].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![0].pois.size}")

                //if(CommonData.getUserAchievementsModel!![0].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![0].pois.size)
                {

                    achievement_icon_one.alpha=1.0f
                    achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_one.alpha=0.5f
                    achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![1].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![1].pois.size}")

                //if(CommonData.getUserAchievementsModel!![1].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![1].pois.size)
                {

                    achievement_icon_two.alpha=1.0f
                    achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_two.alpha=0.5f
                    achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![2].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![2].pois.size}")

                //if(CommonData.getUserAchievementsModel!![2].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![2].pois.size)
                {

                    achievement_icon_three.alpha=1.0f
                    achievement_icon_three_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_three.alpha=0.5f
                    achievement_icon_three_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![3].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![3].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![3].pois.size}")

                //if(CommonData.getUserAchievementsModel!![3].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![3].pois.size)
                {

                    achievement_icon_four.alpha=1.0f
                    achievement_icon_four_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_four.alpha=0.5f
                    achievement_icon_four_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in CommonData.getUserAchievementsModel!![4].pois.indices)
                {
                    if (CommonData.getUserAchievementsModel!![4].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getUserAchievementsModel!![4].pois.size}")

                //if(CommonData.getUserAchievementsModel!![4].ua_id != null)
                if(completedPOI == CommonData.getUserAchievementsModel!![4].pois.size)
                {

                    achievement_icon_five.alpha=1.0f
                    achievement_icon_five_lock_iv.visibility=View.GONE
                }
                else
                {
                    achievement_icon_five.alpha=0.5f
                    achievement_icon_five_lock_iv.visibility=View.VISIBLE
                }

            }
        }
    }

    private fun getTrailDetails() {
        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", sessionHandlerClass.getSession("login_id"))


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getTrailsList(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getTrailsData = response.body()?.message
                        runOnUiThread {


                            if (CommonData.getTrailsData != null) {
                                for (i in CommonData.getTrailsData!!.indices) {
                                    println("Details IF completed_poi_count :::  ${CommonData.getTrailsData!![i].completed_poi_count}")

                                    completedPoiCount += CommonData.getTrailsData!![i].completed_poi_count.toInt()
                                }
                                completed_poi_count.text = "$completedPoiCount"
                                println("Details IF completed_poi_count Overall :::  $completedPoiCount")
                            }

                        }
                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }
}