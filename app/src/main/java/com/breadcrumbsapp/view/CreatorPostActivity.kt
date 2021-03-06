package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.CreatorPostAdapter
import com.breadcrumbsapp.databinding.CreatorPostLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.model.GetFeedDataModel
import com.breadcrumbsapp.model.GetFriendsListModel
import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.creator_post_layout.*
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
    private lateinit var getTrailsModelList: GetTrailsModel.Message
    private lateinit var  profileImage:String
    private lateinit var  userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= CreatorPostLayoutBinding.inflate(layoutInflater)
        sharedPreference = SessionHandlerClass(applicationContext)
        setContentView(binding.root)
        creator_post_rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        getTrailsModelList = intent.extras?.get("getTrailsListData") as GetTrailsModel.Message
        sharedPreference.saveSession("temp_trail_id",getTrailsModelList.id)



        profileImage=resources.getString(R.string.staging_url)+getTrailsModelList.profile_picture
        println("profileImage :: $profileImage")
        Glide.with(applicationContext).load(profileImage).into(creator_icon_iv)
        creator_title_tv.text=getTrailsModelList.username
        userName=getTrailsModelList.username

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
                .baseUrl(resources.getString(R.string.staging_url))
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
            jsonObject.put("user_id", sharedPreference.getSession("login_id"))

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

                        CommonData.getFeedData = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getFeedData != null) {

                                val feedList = ArrayList<GetFeedDataModel.Message>()

                                CommonData.getFeedData?.forEach {
                                    if(it.username=="NIGHT SAFARI")
                                    {
                                        feedList.add(it)
                                    }
                                }
                                creatorPostAdapter = CreatorPostAdapter(feedList,profileImage,userName)
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