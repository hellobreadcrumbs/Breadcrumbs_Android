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

class FriendProfileScreenActivity : AppCompatActivity() {
    private lateinit var feedPostAdapter: FeedPostAdapter
    private var interceptor = intercept()

    //private lateinit var message: GetRankingModel.Message
    private lateinit var binding: ProfileScreenFriendBinding
    var playerLevelString: String = ""
    private lateinit var userID: String
    private lateinit var sessionHandlerClass: SessionHandlerClass
    var completedPOI: Int = 0
    var completedTrail: Int = 0


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

        getMyFeedPostDetails(friendID)
        getUserAchievementsAPI(friendID)

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
                                    if (it.user_id != "54") {
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

                                println("UserAchieve Data: ${CommonData.getFriendAchievementsModel!!.size}")


                                for (i in CommonData.getFriendAchievementsModel!!.indices) {
                                    if (CommonData.getFriendAchievementsModel!![i].ua_id != null) {
                                        ++completedTrail
                                    }

                                    if (CommonData.getFriendAchievementsModel!![i].pois[i].uc_id != null) {
                                        ++completedPOI
                                    }
                                }

                                friend_profile_completed_poi_count.text = completedPOI.toString()
                                friend_profile_completed_trail_count.text =
                                    completedTrail.toString()


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