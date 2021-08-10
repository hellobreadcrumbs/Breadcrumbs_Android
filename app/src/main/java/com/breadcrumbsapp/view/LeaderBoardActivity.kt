package com.breadcrumbsapp.view


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.CustomDropDownAdapter
import com.breadcrumbsapp.adapter.LeaderBoardPlayerListAdapter
import com.breadcrumbsapp.databinding.LeaderBoardActivityLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
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
import java.util.*
import java.util.concurrent.TimeUnit

class LeaderBoardActivity : AppCompatActivity() {
    private var interceptor = intercept()
    private lateinit var binding: LeaderBoardActivityLayoutBinding
    private lateinit var leaderBoardPlayerListAdapter: LeaderBoardPlayerListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LeaderBoardActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        leaderboardScreenBackButton.setOnClickListener { finish() }
        leaderBoard_player_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        getRankingDetails()


        val customDropDownAdapter = CustomDropDownAdapter(applicationContext)
        spinner.adapter=customDropDownAdapter
       /* if (spinner != null) {
            val arrayAdapter = ArrayAdapter(this, R.layout.leaderboard_spinner_item, personNames)
            spinner.adapter = arrayAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Toast.makeText(
                        this@LeaderBoardActivity,
                        personNames[position],
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

        }*/




    }


    private fun getRankingDetails() {
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
            jsonObject.put("trail_id", "4")

            println("getUserDetails Url = ${resources.getString(R.string.staging_url)}")
            println("getUserDetails Input = $jsonObject")


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
                                leaderBoardPlayerListAdapter = LeaderBoardPlayerListAdapter(CommonData.getRankData!!)
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
    fun calculateRanking(expStr : String)
    {
        var ranking = "Recruit"
        var level = 1
        var base = 0
        var nextLevel = 1000


        val exp=expStr.toInt()
        when (exp) {
            in 1000..1999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 2
                base = 1000
                nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 3
                base = 2000
                nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 4
                base = 3000
                nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 5
                base = 4000
                nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 6
                base = 6000
                nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 7
                base = 8000
                nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 8
                base = 10000
                nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "Recruit"
                level = 9
                base = 12000
                nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 10
                base = 14000
                nextLevel = 17000
                //    badge = badge2
            }
            in 17000..20499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 11
                base = 17000
                nextLevel = 20500
                //    badge = badge2
            }
            in 20500..24499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 12
                base = 20500
                nextLevel = 24500
                //    badge = badge2
            }
            in 24500..28499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 13
                base = 24500
                nextLevel = 28500
                //   badge = badge2
            }
            in 28500..33499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 14
                base = 28500
                nextLevel = 33500
                //   badge = badge2
            }
            in 33500..38999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 15
                base = 33500
                nextLevel = 39000
                //   badge = badge2
            }
            in 39000..44999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 16
                base = 39000
                nextLevel = 45000
                //   badge = badge2
            }
            in 45000..51499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 17
                base = 45000
                nextLevel = 51500
                // badge = badge2
            }
            in 51500..58499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 18
                base = 51500
                nextLevel = 58500
                // badge = badge2
            }
            in 58500..65999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 19
                base = 58500
                nextLevel = 66000
                // badge = badge2
            }
            in 66000..73999 -> { // 2000 thresh
                ranking = "Captain"
                level = 20
                base = 66000
                nextLevel = 74000
                //   badge = badge3
            }
        }

        val percent = (exp - base) / (nextLevel - base) * 100
        val expToLevel = nextLevel - base - (exp - base)


    }
}