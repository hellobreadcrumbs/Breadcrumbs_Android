package com.breadcrumbsapp.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.TrailsListScreenAdapter
import com.breadcrumbsapp.databinding.TrailsScreenLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.model.GetTrailsModel
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.google.gson.Gson
import kotlinx.android.synthetic.main.trails_screen_layout.*
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

class TrailScreenActivity:AppCompatActivity()
{
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private var interceptor = intercept()
    private lateinit var binding:TrailsScreenLayoutBinding
    private lateinit var trailsListScreenAdapter:TrailsListScreenAdapter
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
        binding= TrailsScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass= SessionHandlerClass(applicationContext)
        trailsScreenRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        if (CommonData.getTrailsData != null) {
            // feedListAdapter = FeedPostAdapter(CommonData.getFeedData!!)
            //feedList.adapter = feedListAdapter

            trailsListScreenAdapter = TrailsListScreenAdapter(CommonData.getTrailsData!!)
            trailsScreenRecyclerView.adapter = trailsListScreenAdapter

        }
        else
        {

          //  getTrailDetails()

            val jsonFileString = readJsonFromAssets(applicationContext, "trails.json")
            getTrailsData=   Gson().fromJson(jsonFileString, GetTrailsModel::class.java)
            print("CommonData.getTrailsData = ${jsonFileString.toString()}")
            CommonData.getTrailsData=getTrailsData.message

        }
       // trailsListScreenAdapter = TrailsListScreenAdapter()
        //trailsScreenRecyclerView.adapter = trailsListScreenAdapter

        trails_screen_back_button.setOnClickListener {
            finish()
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

                        runOnUiThread {

                            if (CommonData.getTrailsData != null) {
                               // feedListAdapter = FeedPostAdapter(CommonData.getFeedData!!)
                                //feedList.adapter = feedListAdapter

                                trailsListScreenAdapter = TrailsListScreenAdapter(CommonData.getTrailsData!!)
                                trailsScreenRecyclerView.adapter = trailsListScreenAdapter

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