package com.breadcrumbsapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.breadcrumbsapp.R
import com.breadcrumbsapp.interfaces.APIService

import com.breadcrumbsapp.model.RecommendedFriendsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.connection.ConnectInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class SearchFirendsViewModel: ViewModel() {
    val firendsList = MutableLiveData<List<RecommendedFriendsModel.Message>>()
     var mContext : Context? = null

    private var interceptor = intercept()

   lateinit var okHttpClient : OkHttpClient.Builder
    lateinit var retrofit : Retrofit.Builder
    lateinit var   apiService : APIService

    fun setApi(context: Context){
        mContext = context
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mContext?.resources?.getString(R.string.live_url))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(APIService::class.java)

    }



    fun getFirends(){
        val jsonObject = JSONObject()
        jsonObject.put("id", "66")

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getRecommendedFriends(
                mContext?.resources?.getString(R.string.api_access_token)?:"",
                requestBody
            )
            if (response.isSuccessful) {
                if (response.body()!!.status) {
                    val resObj = response?.body()?.message
                    firendsList.postValue( resObj)
                }
            }else{
                firendsList.postValue( null)
            }

        }



    }



    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }
}