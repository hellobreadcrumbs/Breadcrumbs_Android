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
import com.breadcrumbsapp.adapter.CreatorPostAdapter
import com.breadcrumbsapp.databinding.UserProfileScreenLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.MyAchievementScreenActivity
import com.breadcrumbsapp.view.MyFriendsListScreenActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.achievement_unlock_details_screen_layout.*
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
    var completedPOI: Int = 0
    var completedTrail: Int = 0
    private lateinit var creatorPostAdapter: CreatorPostAdapter
    private var interceptor = intercept()
    private lateinit var binding: UserProfileScreenLayoutBinding
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private lateinit var getTrailsData:GetTrailsModel


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
      //  getTrailDetails()
        setOnClickListeners()

        val jsonFileString = readJsonFromAssets(applicationContext, "trails.json")
        getTrailsData=   Gson().fromJson(jsonFileString, GetTrailsModel::class.java)
        //print("CommonData.getTrailsData = ${jsonFileString.toString()}")
        CommonData.getTrailsData=getTrailsData.message


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

        if (sessionHandlerClass.getSession("player_photo_url") != null && sessionHandlerClass.getSession(
                "player_photo_url"
            ) != ""
        ) {
            Glide.with(applicationContext).load(sessionHandlerClass.getSession("player_photo_url"))
                .into(profile_edit_screen_profile_pic_iv)
        } else {
            Glide.with(applicationContext).load(R.drawable.no_image)
                .into(profile_edit_screen_profile_pic_iv)
        }
        user_profile_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        my_friends_list_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, MyFriendsListScreenActivity::class.java))
        })

        achievementLayout.setOnClickListener(View.OnClickListener {


            startActivity(Intent(applicationContext, MyAchievementScreenActivity::class.java))


        })
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
                .baseUrl(resources.getString(R.string.live_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
              jsonObject.put("user_id", sessionHandlerClass.getSession("login_id"))
           // jsonObject.put("user_id", "198")

            println("getFeedPostData Url = ${resources.getString(R.string.live_url)}")
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
                                creatorPostAdapter = CreatorPostAdapter(CommonData.getMyFeedData!!)
                                profile_screen_user_post_list.adapter = creatorPostAdapter

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
                .baseUrl(resources.getString(R.string.live_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


            val jsonObject = JSONObject()
            jsonObject.put("user_id", "66")

            println("getUserAchievementsAPI Url = ${resources.getString(R.string.live_url)}")
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

                        runOnUiThread {

                            if (CommonData.getUserAchievementsModel != null) {

                                println("UserAchieve Data: ${CommonData.getUserAchievementsModel!!.size}")


                                for (i in CommonData.getUserAchievementsModel!!.indices) {
                                    if (CommonData.getUserAchievementsModel!![i].ua_id != null) {
                                        ++completedTrail
                                    }

                                    if (CommonData.getUserAchievementsModel!![i].pois[i].uc_id != null) {
                                        ++completedPOI
                                    }
                                }

                                completed_poi_count.text = completedPOI.toString()
                                completed_trail_count.text = completedTrail.toString()
                                profile_screen_user_name.text =
                                    sessionHandlerClass.getSession("player_user_name")

                                profile_screen_user_level.text=sessionHandlerClass.getSession("level_text_value")
                                profile_screen_xp_point_value.text="${sessionHandlerClass.getSession("player_experience_points")} XP"

                                var progressBarMaxValue=sessionHandlerClass.getIntegerSession("xp_point_nextLevel_value")
                                var expToLevel=sessionHandlerClass.getIntegerSession("expTo_level_value")
                                var completedPoints=sessionHandlerClass.getSession("player_experience_points")
                                val levelValue=sessionHandlerClass.getSession("lv_value")

                                xp_to_next_level.text="$expToLevel XP TO $levelValue"

                                profile_screen_progress_bar.max=progressBarMaxValue
                                ObjectAnimator.ofInt(profile_screen_progress_bar, "progress", completedPoints!!.toInt())
                                    .setDuration(1000)
                                    .start()

                            }

                        }


                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
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
                .baseUrl(resources.getString(R.string.live_url))
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