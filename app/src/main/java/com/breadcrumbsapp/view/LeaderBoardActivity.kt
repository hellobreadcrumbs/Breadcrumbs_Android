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
    private var trailID:String="4"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LeaderBoardActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass = SessionHandlerClass(applicationContext)

        leaderboardScreenBackButton.setOnClickListener { finish() }
        leaderBoard_player_list.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

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



        if (sessionHandlerClass.getSession("player_photo_url") != null && sessionHandlerClass.getSession(
                "player_photo_url"
            ) != ""
        ) {
            Glide.with(applicationContext).load(sessionHandlerClass.getSession("player_photo_url"))
                .into(leader_board_player_profile_pic)
        } else {
            Glide.with(applicationContext).load(R.drawable.no_image)
                .into(leader_board_player_profile_pic)
        }


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


    private fun getRankingDetails(trailID:String) {
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
        if(position==0)
        {
            trailID="4"

        }
        else if (position==1)
        {
            trailID="6"
        }
        getRankingDetails(trailID)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}