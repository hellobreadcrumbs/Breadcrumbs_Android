package com.breadcrumbsapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.FeedPostAdapter
import com.breadcrumbsapp.databinding.FeedLayoutBinding

import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.profile.ProfileScreenActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.feed_layout.*
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

class FeedPostActivity : AppCompatActivity() {
    private var interceptor = intercept()
    private lateinit var binding: FeedLayoutBinding
    private lateinit var feedPostAdapter: FeedPostAdapter
    private lateinit var sharedPreference: SessionHandlerClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FeedLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SessionHandlerClass(applicationContext)
        feedList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getFeedPostData()

        feedScreenBackButton.setOnClickListener {
            finish()
        }

        if(sharedPreference.getSession("player_photo_url")!=null && sharedPreference.getSession("player_photo_url")!="")
        {
            Glide.with(applicationContext).load(sharedPreference.getSession("player_photo_url")).into(FeedScreenUserProfilePicture)

        }
        else
        {
            Glide.with(applicationContext).load(R.drawable.no_image).into(FeedScreenUserProfilePicture)
        }

        FeedScreenUserProfilePicture.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileScreenActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            ))
        }
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
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
           jsonObject.put("user_id", sharedPreference.getSession("login_id"))
          //  jsonObject.put("user_id","66")


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

                        CommonData.getFeedData = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getFeedData != null) {
                                feedPostAdapter = FeedPostAdapter(CommonData.getFeedData!!,sharedPreference.getSession("login_id"))
                                feedList.adapter = feedPostAdapter

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