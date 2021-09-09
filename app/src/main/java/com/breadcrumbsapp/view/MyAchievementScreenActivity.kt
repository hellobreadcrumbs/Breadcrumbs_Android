package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.MyAchievementsListAdapter
import com.breadcrumbsapp.databinding.MyAchievementsLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.CommonData.Companion.getUserAchievementsModel
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.my_achievements_layout.*
import kotlinx.android.synthetic.main.user_profile_screen_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class MyAchievementScreenActivity : AppCompatActivity() {
    private lateinit var binding: MyAchievementsLayoutBinding
    private lateinit var myAchievementsListAdapter: MyAchievementsListAdapter
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private var interceptor = intercept()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyAchievementsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionHandlerClass= SessionHandlerClass(applicationContext)



        my_achievements_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        if(CommonData.getTrailsData ==null)
        {
            getTrailDetails()
        }

        myAchievements_List.layoutManager=LinearLayoutManager(applicationContext,RecyclerView.VERTICAL,false)
        myAchievementsListAdapter= MyAchievementsListAdapter(getUserAchievementsModel!!,CommonData.getTrailsData!!)
        myAchievements_List.adapter=myAchievementsListAdapter

        arrangeAchievements()



    }
    private fun arrangeAchievements()
    {
        var completedPOI=0
        when (getUserAchievementsModel!!.size) {
            1 -> {
                val badgeImg =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg).into(my_achievement_icon_one)

                my_achievement_icon_two.visibility=View.GONE
                my_achievement_icon_two_lock_iv.visibility=View.VISIBLE
                my_achievement_icon_three.visibility=View.GONE
                my_achievement_icon_three_lock_iv.visibility=View.VISIBLE
                my_achievement_icon_four.visibility=View.GONE
                my_achievement_icon_four_lock_iv.visibility=View.VISIBLE
                my_achievement_icon_five.visibility=View.GONE
                my_achievement_icon_five_lock_iv.visibility=View.VISIBLE


                completedPOI=0
                for (j in  getUserAchievementsModel!![0].pois.indices)
                {
                    if ( getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![0].pois.size)
                {

                    my_achievement_icon_one.alpha=1.0f
                    my_achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_one.alpha=0.5f
                    my_achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }


            }
            2 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(my_achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(my_achievement_icon_two)


                my_achievement_icon_three.visibility=View.GONE
                my_achievement_icon_three_lock_iv.visibility=View.VISIBLE
                my_achievement_icon_four.visibility=View.GONE
                my_achievement_icon_four_lock_iv.visibility=View.VISIBLE
                my_achievement_icon_five.visibility=View.GONE
                my_achievement_icon_five_lock_iv.visibility=View.VISIBLE


                completedPOI=0
                for (j in  getUserAchievementsModel!![0].pois.indices)
                {
                    if ( getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![0].pois.size)
                {

                    my_achievement_icon_one.alpha=1.0f
                    my_achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_one.alpha=0.5f
                    my_achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in  getUserAchievementsModel!![1].pois.indices)
                {
                    if ( getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![1].pois.size)
                {

                    my_achievement_icon_two.alpha=1.0f
                    my_achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_two.alpha=0.5f
                    my_achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }
            }
            3 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(my_achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(my_achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(my_achievement_icon_three)

                my_achievement_icon_four.visibility=View.GONE
                my_achievement_icon_four_lock_iv.visibility=View.VISIBLE
                my_achievement_icon_five.visibility=View.GONE
                my_achievement_icon_five_lock_iv.visibility=View.VISIBLE

                completedPOI=0
                for (j in  getUserAchievementsModel!![0].pois.indices)
                {
                    if ( getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![0].pois.size)
                {

                    my_achievement_icon_one.alpha=1.0f
                    my_achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_one.alpha=0.5f
                    my_achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in  getUserAchievementsModel!![1].pois.indices)
                {
                    if ( getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![1].pois.size)
                {

                    my_achievement_icon_two.alpha=1.0f
                    my_achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_two.alpha=0.5f
                    my_achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in  getUserAchievementsModel!![2].pois.indices)
                {
                    if ( getUserAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![2].pois.size)
                {

                    my_achievement_icon_three.alpha=1.0f
                    my_achievement_icon_three_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_three.alpha=0.5f
                    my_achievement_icon_three_lock_iv.visibility=View.VISIBLE
                }
            }
            4 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(my_achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(my_achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(my_achievement_icon_three)

                val badgeImg4 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![3].badge_img
                Glide.with(applicationContext).load(badgeImg4).into(my_achievement_icon_four)


                my_achievement_icon_five.visibility=View.GONE
                my_achievement_icon_five_lock_iv.visibility=View.VISIBLE


                completedPOI=0
                for (j in  getUserAchievementsModel!![0].pois.indices)
                {
                    if ( getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![0].pois.size)
                {

                    my_achievement_icon_one.alpha=1.0f
                    my_achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_one.alpha=0.5f
                    my_achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in  getUserAchievementsModel!![1].pois.indices)
                {
                    if ( getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![1].pois.size)
                {

                    my_achievement_icon_two.alpha=1.0f
                    my_achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_two.alpha=0.5f
                    my_achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in  getUserAchievementsModel!![2].pois.indices)
                {
                    if ( getUserAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![2].pois.size)
                {

                    my_achievement_icon_three.alpha=1.0f
                    my_achievement_icon_three_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_three.alpha=0.5f
                    my_achievement_icon_three_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in  getUserAchievementsModel!![3].pois.indices)
                {
                    if ( getUserAchievementsModel!![3].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![3].pois.size)
                {

                    my_achievement_icon_four.alpha=1.0f
                    my_achievement_icon_four_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_four.alpha=0.5f
                    my_achievement_icon_four_lock_iv.visibility=View.VISIBLE
                }

            }
            5 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(my_achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(my_achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(my_achievement_icon_three)

                val badgeImg4 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![3].badge_img
                Glide.with(applicationContext).load(badgeImg4).into(my_achievement_icon_four)

                val badgeImg5 =
                    resources.getString(R.string.staging_url) + getUserAchievementsModel!![4].badge_img
                Glide.with(applicationContext).load(badgeImg5).into(my_achievement_icon_five)


                completedPOI=0
                for (j in  getUserAchievementsModel!![0].pois.indices)
                {
                    if ( getUserAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![0].pois.size)
                {

                    my_achievement_icon_one.alpha=1.0f
                    my_achievement_icon_one_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_one.alpha=0.5f
                    my_achievement_icon_one_lock_iv.visibility=View.VISIBLE
                }

                completedPOI=0
                for (j in  getUserAchievementsModel!![1].pois.indices)
                {
                    if ( getUserAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![1].pois.size)
                {

                    my_achievement_icon_two.alpha=1.0f
                    my_achievement_icon_two_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_two.alpha=0.5f
                    my_achievement_icon_two_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in  getUserAchievementsModel!![2].pois.indices)
                {
                    if ( getUserAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![2].pois.size)
                {

                    my_achievement_icon_three.alpha=1.0f
                    my_achievement_icon_three_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_three.alpha=0.5f
                    my_achievement_icon_three_lock_iv.visibility=View.VISIBLE
                }
                completedPOI=0
                for (j in  getUserAchievementsModel!![3].pois.indices)
                {
                    if ( getUserAchievementsModel!![3].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                completedPOI=0
                for (j in  getUserAchievementsModel!![4].pois.indices)
                {
                    if ( getUserAchievementsModel!![4].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                if(completedPOI== getUserAchievementsModel!![4].pois.size)
                {

                    my_achievement_icon_four.alpha=1.0f
                    my_achievement_icon_four_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_four.alpha=0.5f
                    my_achievement_icon_four_lock_iv.visibility=View.VISIBLE
                }
                if(getUserAchievementsModel!![4].ua_id != null)
                {

                    my_achievement_icon_five.alpha=1.0f
                    my_achievement_icon_five_lock_iv.visibility=View.GONE
                }
                else
                {
                    my_achievement_icon_five.alpha=0.5f
                    my_achievement_icon_five_lock_iv.visibility=View.VISIBLE
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

                        for(i in CommonData.getTrailsData!!.indices)
                        {

                            println("Details about POIs ::: ${CommonData.getTrailsData!![i].id} == ${CommonData.getTrailsData!![i].completed_poi_count}")


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