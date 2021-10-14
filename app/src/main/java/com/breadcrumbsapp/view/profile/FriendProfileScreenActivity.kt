package com.breadcrumbsapp.view.profile


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.FeedPostAdapter
import com.breadcrumbsapp.databinding.ProfileScreenFriendBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.profile_screen_friend.*
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
import kotlinx.android.synthetic.main.profile_screen_friend.achievement_icon_five as achievement_icon_five1
import kotlinx.android.synthetic.main.profile_screen_friend.achievement_icon_five_lock_iv as achievement_icon_five_lock_iv1
import kotlinx.android.synthetic.main.profile_screen_friend.achievement_icon_four as achievement_icon_four1
import kotlinx.android.synthetic.main.profile_screen_friend.achievement_icon_three as achievement_icon_three1
import kotlinx.android.synthetic.main.user_profile_screen_layout.achievement_icon_four_lock_iv as achievement_icon_four_lock_iv1
import kotlinx.android.synthetic.main.user_profile_screen_layout.achievement_icon_one as achievement_icon_one1
import kotlinx.android.synthetic.main.user_profile_screen_layout.achievement_icon_one_lock_iv as achievement_icon_one_lock_iv1
import kotlinx.android.synthetic.main.user_profile_screen_layout.achievement_icon_three_lock_iv as achievement_icon_three_lock_iv1
import kotlinx.android.synthetic.main.user_profile_screen_layout.achievement_icon_two as achievement_icon_two1
import kotlinx.android.synthetic.main.user_profile_screen_layout.achievement_icon_two_lock_iv as achievement_icon_two_lock_iv1

class FriendProfileScreenActivity : AppCompatActivity() {
    private lateinit var feedPostAdapter: FeedPostAdapter
    private var interceptor = intercept()

    private lateinit var binding: ProfileScreenFriendBinding
    private  var playerLevelString: String = ""
    private lateinit var userID: String
    private lateinit var sessionHandlerClass: SessionHandlerClass

    private var completedPoiCount: Int = 0
    private var completedTrailCount: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileScreenFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass = SessionHandlerClass(applicationContext)
        //message = intent.extras?.get("ChosenFriendDetail") as GetRankingModel.Message
        post_screen_friend_post_list.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)

        //player_level
        playerLevelString = intent.getStringExtra("player_level").toString()
        userID = sessionHandlerClass.getSession("player_id").toString()

        friend_profile_screen_backButton.setOnClickListener {
            finish()
        }


        val username = intent.getStringExtra("username").toString()
        val totalExp = intent.getStringExtra("total_xp").toString()
        val friendID = intent.getStringExtra("friend_id").toString()
        val profilePic = intent.getStringExtra("profile_pic").toString()
        val friendStatus = intent.getStringExtra("friend_status").toString()
        val friendUserID = intent.getStringExtra("friend_user_id").toString()




        println("Friend ID details.. $friendID , ${friendUserID} , $friendStatus")

        selected_player_profile_name.text = "${username}'s Profile"
        selected_player_leaderBoard_playerName.text = username
        friend_profile_screen_postTitle.text = "${username}'S POSTS"
        friends_profile_completed_POIs.text = "$totalExp XP"
        selected_player_leaderBoard_player_level.text = playerLevelString




        if(friendStatus=="1")
        {
            Glide.with(applicationContext).load(R.drawable.selected_player_screen_remove_friend_btn).into(add_friend_btn)
        }
        else
        {
            //selected_player_screen_add_friend_btn
            Glide.with(applicationContext).load(R.drawable.selected_player_screen_add_friend_btn).into(add_friend_btn)
        }


        Glide.with(applicationContext).load("${getString(R.string.staging_url)}${profilePic}")
            .placeholder(
                resources.getDrawable(
                    R.drawable.com_facebook_profile_picture_blank_portrait,
                    null
                )
            ).into(selected_player_userProfilePicture)
        getTrailDetails(friendUserID)
        getMyFeedPostDetails(friendUserID)
        getUserAchievementsAPI(friendUserID)

        add_friend_btn.setOnClickListener {

            if(friendStatus=="1")
            {
                unFriend(friendID)
            }
            else
            {
                addFriend(userID, friendID)
            }

        }

        // displayUiData(message)
    }

    /*private fun displayUiData(getRewardsDataModelMessage: GetRankingModel.Message) {
        with(getRewardsDataModelMessage) {
            selected_player_profile_name.text = "${username}'s Profile"
            selected_player_leaderBoard_playerName.text = username
            friend_profile_screen_postTitle.text = "${username}'S POSTS"
            friends_profile_completed_POIs.text = "$total_exp XP"
            selected_player_leaderBoard_player_level.text = playerLevelString

            getMyFeedPostDetails(id)
            getUserAchievementsAPI(id)

            add_friend_btn.setOnClickListener(View.OnClickListener {
            addFriend(userID,id)
            })


        }
    }*/

    private fun getMyFeedPostDetails(userID: String) {
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
            jsonObject.put("user_id", userID)
            // jsonObject.put("user_id", "198")

            println("getFeedPostData Url = ${resources.getString(R.string.staging_url)}")
            println("getFeedPostData Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getFeedDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )


                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        //   CommonData.getFeedData = response.body()?.message

                        runOnUiThread {
                            /*if (CommonData.getFeedData!!.isNotEmpty()) {
                               // creatorPostAdapter = CreatorPostAdapter(CommonData.getMyFeedData!!)
                               // post_screen_friend_post_list.adapter = creatorPostAdapter

                                feedPostAdapter = FeedPostAdapter(CommonData.getFeedData!!,userID)
                                post_screen_friend_post_list.adapter = feedPostAdapter
                            }
                            else{
                                post_screen_friend_post_list.visibility=View.GONE
                                no_post_text.visibility=View.VISIBLE
                            }*/

                            if (response.body()!!.message != null) {
                                if (CommonData.feedList.size > 0) {
                                    CommonData.feedList.clear()
                                }
                                response.body()?.message?.forEach {

                                    println("Friend Userid REs:: ${it.user_id}")
                                    //  if (it.user_id != "54" || it.user_id !="null"||it.user_id !=null && it.title!=null)
                                   /* if (it.user_id != "54") {
                                        println("************** NAME = ${it.title}")
                                        CommonData.feedList.add(it)

                                    }*/
                                    //userID

                                    if (it.user_id == userID) {
                                        println("************** NAME = ${it.title}")
                                        CommonData.feedList.add(it)

                                    }
                                }
                                if (CommonData.feedList.size > 0) {
                                    post_screen_friend_post_list.visibility = View.VISIBLE
                                    no_post_text.visibility = View.GONE
                                    CommonData.getFeedData = CommonData.feedList

                                    if (CommonData.getFeedData!!.isNotEmpty()) {
                                        feedPostAdapter = FeedPostAdapter(
                                            CommonData.feedList,
                                            userID
                                        )
                                        post_screen_friend_post_list.adapter = feedPostAdapter

                                    }
                                } else {
                                    post_screen_friend_post_list.visibility = View.GONE
                                    no_post_text.visibility = View.VISIBLE
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
    private fun unFriend(friendID: String) {
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
            jsonObject.put("friend_id", friendID)
            println("unFriend API Url = ${resources.getString(R.string.staging_url)}")
            println("unFriend API Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.unFriend(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    // Convert raw JSON to  JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val registerJSON = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    val jsonElement: JsonElement? = JsonParser.parseString(registerJSON)
                    val jsonObject: JsonObject? = jsonElement?.asJsonObject

                    val status: Boolean = jsonObject!!.get("status")!!.asBoolean
                    val message: String = jsonObject.get("message")!!.asString
                    println("unFriend API Status = $status")

                    if (status) {
                        runOnUiThread {
                            println("Friend Profile Screen runOnUIThread...")
                            Glide.with(applicationContext)
                                .load(R.drawable.selected_player_screen_add_friend_btn)
                                .into(add_friend_btn)

                            Toast.makeText(applicationContext,"Removed Successful",Toast.LENGTH_SHORT).show()
                        }
                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addFriend(userID: String, friendID: String) {
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
            jsonObject.put("id", userID)
            jsonObject.put("friend_id", friendID)


            println("addFriend API Url = ${resources.getString(R.string.staging_url)}")
            println("addFriend API Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.addFriend(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    // Convert raw JSON to  JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val registerJSON = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    val jsonElement: JsonElement? = JsonParser.parseString(registerJSON)
                    val jsonObject: JsonObject? = jsonElement?.asJsonObject

                    val status: Boolean = jsonObject!!.get("status")!!.asBoolean
                    val message: String = jsonObject.get("message")!!.asString
                    println("addFriend API Status = $status")

                    if (status) {
                        runOnUiThread {
                            println("Friend Profile Screen runOnUIThread...")
                            Glide.with(applicationContext)
                                .load(R.drawable.selected_player_screen_remove_friend_btn)
                                .into(add_friend_btn)
                        }
                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun loadAchievements() {
        println("load::: ${CommonData.getFriendAchievementsModel!!.size}")

        var completedPOI = 0
        when (CommonData.getFriendAchievementsModel!!.size) {
            1 -> {

                val badgeImg =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg).into(achievement_icon_one)

                achievement_icon_two.visibility = View.GONE
                achievement_icon_two_lock_iv.visibility = View.VISIBLE
                achievement_icon_three.visibility = View.GONE
                achievement_icon_three_lock_iv.visibility = View.VISIBLE
                achievement_icon_four.visibility = View.GONE
                achievement_icon_four_lock_iv.visibility = View.VISIBLE
                achievement_icon_five.visibility = View.GONE
                achievement_icon_five_lock_iv.visibility = View.VISIBLE

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![0].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }

                //if(CommonData.getFriendAchievementsModel!![0].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![0].pois.size) {

                    achievement_icon_one.alpha = 1.0f
                    achievement_icon_one_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_one.alpha = 0.5f
                    achievement_icon_one_lock_iv.visibility = View.VISIBLE
                }


            }
            2 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)


                achievement_icon_three.visibility = View.GONE
                achievement_icon_three_lock_iv.visibility = View.VISIBLE
                achievement_icon_four.visibility = View.GONE
                achievement_icon_four_lock_iv.visibility = View.VISIBLE
                achievement_icon_five.visibility = View.GONE
                achievement_icon_five_lock_iv.visibility = View.VISIBLE

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![0].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![0].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![0].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![0].pois.size) {

                    achievement_icon_one.alpha = 1.0f
                    achievement_icon_one_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_one.alpha = 0.5f
                    achievement_icon_one_lock_iv.visibility = View.VISIBLE
                }

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![1].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![1].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![1].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![1].pois.size) {

                    achievement_icon_two.alpha = 1.0f
                    achievement_icon_two_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_two.alpha = 0.5f
                    achievement_icon_two_lock_iv.visibility = View.VISIBLE
                }
            }
            3 -> {


                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(achievement_icon_three)

                achievement_icon_four.visibility = View.GONE
                achievement_icon_four_lock_iv.visibility = View.VISIBLE
                achievement_icon_five.visibility = View.GONE
                achievement_icon_five_lock_iv.visibility = View.VISIBLE

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![0].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![0].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![0].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![0].pois.size) {

                    achievement_icon_one.alpha = 1.0f
                    achievement_icon_one_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_one.alpha = 0.5f
                    achievement_icon_one_lock_iv.visibility = View.VISIBLE
                }

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![1].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![1].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![1].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![1].pois.size) {

                    achievement_icon_two.alpha = 1.0f
                    achievement_icon_two_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_two.alpha = 0.5f
                    achievement_icon_two_lock_iv.visibility = View.VISIBLE
                }

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![2].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![2].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![2].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![2].pois.size) {

                    achievement_icon_three.alpha = 1.0f
                    achievement_icon_three_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_three.alpha = 0.5f
                    achievement_icon_three_lock_iv.visibility = View.VISIBLE
                }
            }
            4 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(achievement_icon_three)

                val badgeImg4 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![3].badge_img
                Glide.with(applicationContext).load(badgeImg4).into(achievement_icon_four)


                achievement_icon_five.visibility = View.GONE
                achievement_icon_five_lock_iv.visibility = View.VISIBLE


                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![0].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![0].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![0].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![0].pois.size) {

                    achievement_icon_one.alpha = 1.0f
                    achievement_icon_one_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_one.alpha = 0.5f
                    achievement_icon_one_lock_iv.visibility = View.VISIBLE
                }

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![1].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![1].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![1].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![1].pois.size) {

                    achievement_icon_two.alpha = 1.0f
                    achievement_icon_two_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_two.alpha = 0.5f
                    achievement_icon_two_lock_iv.visibility = View.VISIBLE
                }
                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![2].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![2].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![2].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![2].pois.size) {

                    achievement_icon_three.alpha = 1.0f
                    achievement_icon_three_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_three.alpha = 0.5f
                    achievement_icon_three_lock_iv.visibility = View.VISIBLE
                }
                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![3].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![3].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![3].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![3].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![3].pois.size) {

                    achievement_icon_four.alpha = 1.0f
                    achievement_icon_four_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_four.alpha = 0.5f
                    achievement_icon_four_lock_iv.visibility = View.VISIBLE
                }

            }
            5 -> {
                val badgeImg1 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![0].badge_img
                Glide.with(applicationContext).load(badgeImg1).into(achievement_icon_one)

                val badgeImg2 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![1].badge_img
                Glide.with(applicationContext).load(badgeImg2).into(achievement_icon_two)

                val badgeImg3 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![2].badge_img
                Glide.with(applicationContext).load(badgeImg3).into(achievement_icon_three)

                val badgeImg4 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![3].badge_img
                Glide.with(applicationContext).load(badgeImg4).into(achievement_icon_four)

                val badgeImg5 =
                    resources.getString(R.string.staging_url) + CommonData.getFriendAchievementsModel!![4].badge_img
                Glide.with(applicationContext).load(badgeImg5).into(achievement_icon_five)


                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![0].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![0].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![0].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![0].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![0].pois.size) {

                    achievement_icon_one.alpha = 1.0f
                    achievement_icon_one_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_one.alpha = 0.5f
                    achievement_icon_one_lock_iv.visibility = View.VISIBLE
                }

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![1].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![1].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![1].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![1].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![1].pois.size) {

                    achievement_icon_two.alpha = 1.0f
                    achievement_icon_two_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_two.alpha = 0.5f
                    achievement_icon_two_lock_iv.visibility = View.VISIBLE
                }

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![2].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![2].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![2].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![2].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![2].pois.size) {

                    achievement_icon_three.alpha = 1.0f
                    achievement_icon_three_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_three.alpha = 0.5f
                    achievement_icon_three_lock_iv.visibility = View.VISIBLE
                }

                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![3].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![3].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![3].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![3].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![3].pois.size) {

                    achievement_icon_four.alpha = 1.0f
                    achievement_icon_four_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_four.alpha = 0.5f
                    achievement_icon_four_lock_iv.visibility = View.VISIBLE
                }
                completedPOI = 0
                for (j in CommonData.getFriendAchievementsModel!![4].pois.indices) {
                    if (CommonData.getFriendAchievementsModel!![4].pois[j].uc_id != null) {
                        ++completedPOI
                    }
                }
                println("POI = $completedPOI == ${CommonData.getFriendAchievementsModel!![4].pois.size}")

                //if(CommonData.getFriendAchievementsModel!![4].ua_id != null)
                if (completedPOI == CommonData.getFriendAchievementsModel!![4].pois.size) {

                    achievement_icon_five.alpha = 1.0f
                    achievement_icon_five_lock_iv.visibility = View.GONE
                } else {
                    achievement_icon_five.alpha = 0.5f
                    achievement_icon_five_lock_iv.visibility = View.VISIBLE
                }

            }
        }
    }
    private fun getUserAchievementsAPI(friendID: String) {
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
            jsonObject.put("user_id", friendID)

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

                        CommonData.getFriendAchievementsModel = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getFriendAchievementsModel != null) {
                                loadAchievements()

                            }

                        }


                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getTrailDetails(friendUserID:String) {
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
            jsonObject.put("user_id", friendUserID)


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

                                friend_profile_completed_poi_count.text = "$completedPoiCount"
                                friend_profile_completed_trail_count.text = "$completedTrailCount"
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