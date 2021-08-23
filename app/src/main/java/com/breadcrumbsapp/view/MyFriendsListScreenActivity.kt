package com.breadcrumbsapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.GetFriendListAdapter
import com.breadcrumbsapp.databinding.FriendsListLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.bumptech.glide.Glide
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
    private lateinit var getFriendListAdapter:GetFriendListAdapter
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

        new_request_layout.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, NewFriendRequestAct::class.java))
        })


    }

    private fun getFeedPostData() {
        try {
            Glide.with(applicationContext).load(R.raw.loading).into(friend_list_screen_loaderImage)
            friend_list_screen_loaderImage.visibility= View.VISIBLE
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
            jsonObject.put("id", "66")

            println("getFeedPostData Url = ${resources.getString(R.string.live_url)}")
            println("getFeedPostData Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getUserFriendList(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getFriendListModel = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getFriendListModel != null) {

                                println("Friends Count :: ${CommonData.getFriendListModel!!.size}")
                                friend_list_screen_loaderImage.visibility=View.GONE

                                getFriendListAdapter = GetFriendListAdapter(CommonData.getFriendListModel!!)
                                friends_list_rv.adapter = getFriendListAdapter

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