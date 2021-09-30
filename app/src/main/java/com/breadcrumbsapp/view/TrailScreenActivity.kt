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


            trailsListScreenAdapter = TrailsListScreenAdapter(CommonData.getTrailsData!!)
            trailsScreenRecyclerView.adapter = trailsListScreenAdapter

        }

        trails_screen_back_button.setOnClickListener {
            sessionHandlerClass.saveSession("clicked_button", "no_reload")
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sessionHandlerClass.saveSession("clicked_button", "no_reload")
    }




    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }
}