package com.breadcrumbsapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.FeedPostAdapter
import com.breadcrumbsapp.databinding.ChallengeActivityBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.challenge_activity.*
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


class ChallengeActivity : AppCompatActivity() {

    lateinit var binding: ChallengeActivityBinding
    lateinit var sharedPreference: SessionHandlerClass
    private var interceptor = intercept()
    private lateinit var poiImage:String
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChallengeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SessionHandlerClass(applicationContext)
        poiNameTextView.text = sharedPreference.getSession("selectedPOIName")


        val bundle: Bundle = intent.extras!!
        val challengeName = bundle.getString("challengeName")
          poiImage = bundle.getString("poiImage") as String

        sharedPreference.saveSession("poi_image", poiImage)

        Glide.with(applicationContext).load(poiImage).into(binding.selfieImageView)

        println("challengeName :: $challengeName")

        if (challengeName == "quiz") {
            challengeIcon.setImageDrawable(getDrawable(R.drawable.quiz_challenge_icon))
            challengeTitle.text = resources.getString(R.string.quiz_challenge)
            subTileOfBeginChallenge.text =
                resources.getString(R.string.quiz_challenge_static_content)
            questionTwoLabel.text = "Correct Answer"
        } else if (challengeName == "selfie") {
            challengeIcon.setImageDrawable(getDrawable(R.drawable.selfie_challenge_icon))
            challengeTitle.text = resources.getString(R.string.selfie_challenge)
            subTileOfBeginChallenge.text =
                resources.getString(R.string.selfie_challenge_static_content)
            questionTwoLabel.text = "Selfie Posted"
        }

        challenge_backButton.setOnClickListener {
            finish()
        }

        binding.beginButton.setOnClickListener {
            when (challengeName) {
                "quiz" -> {


                   // beginChallengeAPI()

                  /*  CommonData.getBeginChallengeModel!!.achievement=""
                    CommonData.getBeginChallengeModel!!.completed_trail=false
                    println("Begin Challenge :: ${CommonData.getBeginChallengeModel!!.achievement}")
                    println("Begin Challenge :: ${CommonData.getBeginChallengeModel!!.completed_trail}")*/

                    startActivity(
                        Intent(
                            this@ChallengeActivity,
                            QuizChallengeQuestionActivity::class.java
                        ).putExtra("poiImage", poiImage)
                    )
                }
                "selfie" -> {
                    startActivity(
                        Intent(
                            this@ChallengeActivity,
                            com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
                        )
                    )
                }
            }
        }

    }
    private fun beginChallengeAPI() {
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
            jsonObject.put("poi_id", sharedPreference.getSession("selectedPOIID"))
            //  jsonObject.put("user_id","66")


            println("beginChallenge Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.beginChallenge(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                try {
                    if (response.isSuccessful) {
                        if (response.body()!!.status) {

                            if(response.body()?.message!!.equals("0"))
                            {


                                runOnUiThread {

                                    if (response.body()?.message!!.equals("0")) {
                                        startActivity(
                                            Intent(
                                                this@ChallengeActivity,
                                                QuizChallengeQuestionActivity::class.java
                                            ).putExtra("poiImage", poiImage)
                                        )
                                    } else {
                                        CommonData.getBeginChallengeModel = response.body()?.message
                                        println("Begin Challenge :: ${CommonData.getBeginChallengeModel!!.achievement}")
                                        println("Begin Challenge :: ${CommonData.getBeginChallengeModel!!.completed_trail}")
                                    }



                                    startActivity(
                                        Intent(
                                            this@ChallengeActivity,
                                            QuizChallengeQuestionActivity::class.java
                                        ).putExtra("poiImage", poiImage)
                                    )


                                }
                            }
                            else{
                                startActivity(
                                    Intent(
                                        this@ChallengeActivity,
                                        QuizChallengeQuestionActivity::class.java
                                    ).putExtra("poiImage", poiImage)
                                )
                            }
                            startActivity(
                                Intent(
                                    this@ChallengeActivity,
                                    QuizChallengeQuestionActivity::class.java
                                ).putExtra("poiImage", poiImage)
                            )

                        }

                    }
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                    startActivity(
                        Intent(
                            this@ChallengeActivity,
                            QuizChallengeQuestionActivity::class.java
                        ).putExtra("poiImage", poiImage)
                    )
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