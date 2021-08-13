package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.FeedPostAdapter
import com.breadcrumbsapp.adapter.RecommendedFriendsAdapter
import com.breadcrumbsapp.databinding.FriendsListLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import kotlinx.android.synthetic.main.feed_layout.*
import kotlinx.android.synthetic.main.feed_layout.feedList
import kotlinx.android.synthetic.main.friends_list_layout.*
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

class MyFriendsListScreenActivity:AppCompatActivity()
{
    private var interceptor = intercept()
    private lateinit var recommendedFriendsAdapter:RecommendedFriendsAdapter
    private lateinit var binding:FriendsListLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= FriendsListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friends_list_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getFeedPostData()

        friends_list_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        add_friend_iv.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext, "Under Construction", Toast.LENGTH_SHORT).show()
        })


    }

    private fun getFeedPostData() {
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
            jsonObject.put("id", "198")

            println("getUserDetails Url = ${resources.getString(R.string.staging_url)}")
            println("getUserDetails Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getRecommendedFriends(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getRecommendedFriendsModel = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getRecommendedFriendsModel != null) {
                                recommendedFriendsAdapter = RecommendedFriendsAdapter(CommonData.getRecommendedFriendsModel!!)
                                friends_list_rv.adapter = recommendedFriendsAdapter

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