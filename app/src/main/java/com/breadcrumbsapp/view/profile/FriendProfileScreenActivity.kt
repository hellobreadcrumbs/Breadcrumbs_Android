package com.breadcrumbsapp.view.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.CreatorPostAdapter
import com.breadcrumbsapp.databinding.ProfileScreenFriendBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.model.GetRankingModel
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
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
    private lateinit var creatorPostAdapter: CreatorPostAdapter
    private var interceptor = intercept()
    private lateinit var message: GetRankingModel.Message
    private lateinit var binding: ProfileScreenFriendBinding
    var playerLevelString: String = ""
    private lateinit var userID: String
    private lateinit var sessionHandlerClass: SessionHandlerClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileScreenFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass= SessionHandlerClass(applicationContext)
        message = intent.extras?.get("ChosenFriendDetail") as GetRankingModel.Message
        post_screen_friend_post_list.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        print("Friend Details =  $message")
        //player_level
        playerLevelString = intent.getStringExtra("player_level").toString()
        userID= sessionHandlerClass.getSession("player_id").toString()

        friend_profile_screen_backButton.setOnClickListener {
            finish()
        }


        displayUiData(message)
    }

    private fun displayUiData(getRewardsDataModelMessage: GetRankingModel.Message) {
        with(getRewardsDataModelMessage) {
            selected_player_profile_name.text = "${username}'s Profile"
            selected_player_leaderBoard_playerName.text = username
            friend_profile_screen_postTitle.text = "${username}'S POSTS"
            friends_profile_completed_POIs.text = "$total_exp XP"
            selected_player_leaderBoard_player_level.text = playerLevelString

            getMyFeedPostDetails(id)

            add_friend_btn.setOnClickListener(View.OnClickListener {
            addFriend(userID,id)
            })


        }
    }

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
                .baseUrl(resources.getString(R.string.live_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", userID)
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
                                post_screen_friend_post_list.adapter = creatorPostAdapter
                            }
                        }

                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun addFriend(userID: String,friendID:String) {
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
            jsonObject.put("id", userID)
            jsonObject.put("friend_id", friendID)
            // jsonObject.put("user_id", "198")

            println("addFriend API Url = ${resources.getString(R.string.live_url)}")
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
                    println("addFriend API message = $message")

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