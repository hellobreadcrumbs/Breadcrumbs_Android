package com.breadcrumbsapp.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.RecommendedFriendsAdapter
import com.breadcrumbsapp.databinding.UserProfileScreenLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.MyAchievementScreenActivity
import com.breadcrumbsapp.view.MyFriendsListScreenActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.friends_list_layout.*
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

class ProfileScreenActivity:AppCompatActivity()
{
    private var interceptor = intercept()
    private lateinit var binding:UserProfileScreenLayoutBinding
    private lateinit var sessionHandlerClass: SessionHandlerClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= UserProfileScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass= SessionHandlerClass(applicationContext)
        setOnClickListeners()
        getUserAchievementsAPI()
    }

    private fun setOnClickListeners()
    {
        edit_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,ProfileEditActivity::class.java).putExtra("from","activity"))
        })

        if(sessionHandlerClass.getSession("player_photo_url")!=null && sessionHandlerClass.getSession("player_photo_url")!="")
        {
            Glide.with(applicationContext).load(sessionHandlerClass.getSession("player_photo_url")).into(profile_edit_screen_profile_pic_iv)
        }
        else
        {
            Glide.with(applicationContext).load(R.drawable.no_image).into(profile_edit_screen_profile_pic_iv)
        }
        user_profile_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        my_friends_list_iv.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,MyFriendsListScreenActivity::class.java))
        })

        achievement_icon_one.setOnClickListener(View.OnClickListener {

            for (i in CommonData.getUserAchievementsModel!!.indices)
            {
                startActivity(Intent(applicationContext, MyAchievementScreenActivity::class.java))
            }


        })
    }


    private fun getUserAchievementsAPI()
    {
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

                        CommonData.getUserAchievementsModel = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getUserAchievementsModel != null) {

                                println("UserAchieve Data: ${CommonData.getUserAchievementsModel!!.size}")
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