package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.CreatorPostAdapter
import com.breadcrumbsapp.adapter.FeedPostAdapter
import com.breadcrumbsapp.databinding.CreatorPostLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import kotlinx.android.synthetic.main.creator_post_layout.*
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


class CreatorPostActivity:AppCompatActivity()
{
    private lateinit var creatorPostAdapter: CreatorPostAdapter
    private lateinit var binding:CreatorPostLayoutBinding
    private var interceptor = intercept()
    private lateinit var sharedPreference: SessionHandlerClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= CreatorPostLayoutBinding.inflate(layoutInflater)
        sharedPreference = SessionHandlerClass(applicationContext)
        setContentView(binding.root)
        creator_post_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getFeedPostData()
        creator_post_back_button.setOnClickListener(View.OnClickListener {
            finish()
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

            // Create JSON using JSONObject
            println(
                "Login : ${
                    sharedPreference.getSession("login_id")
                }"
            )
            val jsonObject = JSONObject()
          //  jsonObject.put("user_id", sharedPreference.getSession("login_id"))
            jsonObject.put("user_id", "198")

            println("getUserDetails Url = ${resources.getString(R.string.live_url)}")
            println("getUserDetails Input = $jsonObject")


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
                                creator_post_rv.adapter = creatorPostAdapter

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