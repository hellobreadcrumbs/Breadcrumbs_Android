package com.breadcrumbsapp.view


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.CustomDropDownAdapter
import com.breadcrumbsapp.adapter.LeaderBoardPlayerListAdapter
import com.breadcrumbsapp.databinding.LeaderBoardActivityLayoutBinding

import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.leader_board_activity_layout.*
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
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class LeaderBoardActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var interceptor = intercept()
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private lateinit var binding: LeaderBoardActivityLayoutBinding
    private lateinit var leaderBoardPlayerListAdapter: LeaderBoardPlayerListAdapter
    private var trailID: String = "4"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LeaderBoardActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass = SessionHandlerClass(applicationContext)

        leaderboardScreenBackButton.setOnClickListener { finish() }
        leaderBoard_player_list.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)


         getUserRankingDetails(trailID)
        if (CommonData.getRankData == null) {

            getRankingDetails(trailID)
        } else {
            loaderImage.visibility = View.GONE

            leaderBoardPlayerListAdapter =
                LeaderBoardPlayerListAdapter(CommonData.getRankData!!)
            leaderBoard_player_list.adapter = leaderBoardPlayerListAdapter
        }

        leaderBoard_playerName.text = sessionHandlerClass.getSession("player_name")
        player_totalXP.text = "${sessionHandlerClass.getSession("player_experience_points")} XP"
        // player_rank.text = "#${sessionHandlerClass.getSession("player_rank")}"
        leaderBoard_player_level.text = sessionHandlerClass.getSession("level_text_value")


        val localProfilePic =
            resources.getString(R.string.staging_url) + sessionHandlerClass.getSession("player_photo_url")

        Glide.with(applicationContext).load(localProfilePic).placeholder(R.drawable.no_image)
            .into(leader_board_player_profile_pic)



        refresh_icon.setOnClickListener {


                getRankingDetails(trailID)

        }


        val customDropDownAdapter = CustomDropDownAdapter(applicationContext)
        spinner.adapter = customDropDownAdapter
        spinner.onItemSelectedListener = this


        share_lay.setOnClickListener {
            val v1: View = window.decorView.rootView.findViewById(R.id.payer_info_lay)

            try {
                // image naming and path  to include sd card  appending name you choose for file
                val now = Date()
                val mPath =
                    externalCacheDir.toString() + "/" + now + ".jpg"

                // create bitmap screen capture
                //  val v1 = window.decorView.rootView
                val bitmap: Bitmap
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap =
                        Bitmap.createBitmap(v1.width, v1.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    v1.draw(canvas)
                } else {
                    v1.isDrawingCacheEnabled = true
                    bitmap = Bitmap.createBitmap(v1.drawingCache)
                    v1.isDrawingCacheEnabled = false
                }


                val imageFile = File(mPath)
                val outputStream = FileOutputStream(imageFile)
                val quality = 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()
                openScreenshot(imageFile)
            } catch (e: Throwable) {
                // Several error may come out with file handling or DOM
                e.printStackTrace()
            }
        }

    }

    private fun openScreenshot(imageFile: File) {

        val photoURI = FileProvider.getUriForFile(
            this,
            applicationContext.packageName.toString() + ".provider",
            imageFile
        )
        println("photoURI $photoURI")
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM, photoURI)

        startActivity(Intent.createChooser(share, "Share Your Design!"))

    }


    private fun getRankingDetails(trailID: String) {
        try {
            Glide.with(applicationContext).load(R.raw.loading).into(loaderImage)
            leaderBoard_player_list.visibility = View.GONE
            loaderImage.visibility = View.VISIBLE
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(50000, TimeUnit.SECONDS)
                .readTimeout(50000, TimeUnit.SECONDS)
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
            jsonObject.put("trail_id", trailID)

            println("getRankingDetails Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getRankingDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getRankData = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getRankData != null) {


                                println("UserName : ${CommonData.getRankData!![0].username}")

                                loaderImage.visibility = View.GONE
                                leaderBoard_player_list.visibility = View.VISIBLE
                                leaderBoardPlayerListAdapter =
                                    LeaderBoardPlayerListAdapter(CommonData.getRankData!!)
                                leaderBoard_player_list.adapter = leaderBoardPlayerListAdapter


                            }

                        }


                    }

                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUserRankingDetails(trailID: String) {
        try {
            Glide.with(applicationContext).load(R.raw.loading).into(loaderImage)
            leaderBoard_player_list.visibility = View.GONE
            loaderImage.visibility = View.VISIBLE
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(50000, TimeUnit.SECONDS)
                .readTimeout(50000, TimeUnit.SECONDS)
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
            jsonObject.put("trail_id", trailID)
            jsonObject.put("user_id", sessionHandlerClass.getSession("login_id"))

            println("getRankingDetails Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getUserRankingDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getUserRankData = response.body()?.message

                        runOnUiThread {

                            if (CommonData.getUserRankData != null) {
                                println("UserName : ${CommonData.getUserRankData!!.username}")
                                if(CommonData.getUserRankData!!.total_exp==null||CommonData.getUserRankData!!.total_exp=="null")
                                {
                                    calculatingXPValues(250)
                                }
                                else
                                {
                                    calculatingXPValues(CommonData.getUserRankData!!.total_exp.toInt())
                                }

                                if(CommonData.getUserRankData!!.total_duration==null || CommonData.getUserRankData!!.total_duration=="null")
                                {
                                    calculateTime(0)
                                }
                                else
                                {
                                    calculateTime(CommonData.getUserRankData!!.total_duration.toLong())
                                }

                                if(CommonData.getUserRankData!!.total_exp==null||CommonData.getUserRankData!!.total_exp=="null")
                                {
                                    player_totalXP.text = "250 XP"
                                }
                                else
                                {
                                    player_totalXP.text = "${CommonData.getUserRankData!!.total_exp} XP"
                                }
                                if (trailID == "4") {
                                    completed_POIs.text =
                                        "${CommonData.getUserRankData!!.total_completed}/11 POIs"
                                } else if (trailID == "6") {
                                    completed_POIs.text =
                                        "${CommonData.getUserRankData!!.total_completed}/10 POIs"
                                }


                            }

                        }


                    }

                }
                getRankingDetails(trailID)

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun calculateTime(seconds: Long) {
        println("LeaderBoard seconds => $seconds")
        if(seconds>0)
        {
            val day = TimeUnit.SECONDS.toDays(seconds).toInt()
            val hours = TimeUnit.SECONDS.toHours(seconds) - day * 24
            val minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60
            val second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60
            println("Day-> $day Hour-> $hours Minute-> $minute Seconds-> $second")

            // total_duration.text = "$day D $hours H"
            total_duration.text = "${day}D ${hours}H"
        }
        else
        {
            total_duration.text = "${"0"}D ${"0"}H"
        }

    }

    private fun calculatingXPValues(exp: Int) {
        var ranking = ""
        var level = 0
       /* var base = 0
        var nextLevel = 0*/
        when (exp) {
            in 0..999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 1
                //base = 1000
                //nextLevel = 2000
            }
            in 1000..1999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 2
                //base = 1000
                //nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 3
                //base = 2000
                //nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 4
                //base = 3000
                //nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 5
               // base = 4000
               // nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 6
                //base = 6000
               // nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 7
               // base = 8000
                //nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 8
               // base = 10000
               // nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 9
                //base = 12000
               // nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "NAVIGATOR"
                level = 10
               // base = 14000
               // nextLevel = 17000

            }
            in 17000..20499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 11
                //base = 17000
                //nextLevel = 20500

            }
            in 20500..24499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 12
              //  base = 20500
               // nextLevel = 24500

            }
            in 24500..28499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 13
               // base = 24500
                //nextLevel = 28500

            }
            in 28500..33499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 14
                //base = 28500
                //nextLevel = 33500

            }
            in 33500..38999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 15
                //base = 33500
                //nextLevel = 39000

            }
            in 39000..44999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 16
               // base = 39000
               // nextLevel = 45000

            }
            in 45000..51499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 17
               // base = 45000
                //nextLevel = 51500

            }
            in 51500..58499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 18
                //base = 51500
                //nextLevel = 58500

            }
            in 58500..65999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 19
                //base = 58500
                //nextLevel = 66000

            }
            in 66000..73999 -> { // 2000 thresh
                ranking = "Captain"
                level = 20
                //base = 66000
                //nextLevel = 74000

            }
        }
        leaderBoard_player_level.text = "$ranking Lv. $level"
    }

    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }


    private var trailIcons = intArrayOf(
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon
    )

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        binding.leaderBoardTrailIcon.setImageResource(trailIcons[position])
        println("leaderBoardTrailIcon POS= $position")
        if (position == 0) {
            trailID = "4"

        } else if (position == 1) {
            trailID = "6"
        }

        getUserRankingDetails(trailID)

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}