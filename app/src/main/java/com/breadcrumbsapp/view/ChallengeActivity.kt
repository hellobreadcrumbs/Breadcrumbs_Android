package com.breadcrumbsapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.ChallengeActivityBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.challenge_activity.*
import kotlinx.android.synthetic.main.discover_details_screen.*
import kotlinx.android.synthetic.main.feed_layout.*
import kotlinx.android.synthetic.main.quiz_challenge_question_activity.*
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

    private var poiQrCode = ""
    private var challengeName = ""
    private var poiImage = ""
    private var poiArId = ""

    private var selectedPOIID: String = ""
    private var selectedTrailID: String = ""
    private var noOfQuestions: String = ""

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChallengeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SessionHandlerClass(applicationContext)
        selectedPOIID = sharedPreference.getSession("selectedPOIID").toString()
        selectedTrailID = sharedPreference.getSession("selected_trail_id").toString()
        noOfQuestions= sharedPreference.getSession("noOfQuestions").toString()

        sharedPreference.saveSession("clicked_button", "")
        poiNameTextView.text = sharedPreference.getSession("selectedPOIName")

        val bundle: Bundle = intent.extras!!

        poiImage = bundle.getString("poiImage") as String
        poiQrCode = bundle.getString("poiQrCode").toString()
        challengeName = bundle.getString("challengeName").toString()
        poiArId = bundle.getString("poiArid").toString()

        sharedPreference.saveSession("poi_image", poiImage)

        //  Glide.with(applicationContext).load(poiImage).into(binding.selfieImageView)

      //  beginChallengeAPI()

        try {


            Glide.with(applicationContext)
                .load(poiImage)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        challenge_screen_loader.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        challenge_screen_loader.visibility = View.GONE
                        return false
                    }
                })
                .into(selfieImageView)

        } catch (e: Exception) {
            e.printStackTrace()
        }


        println("selectedPOIDiscoveryXP => Challenge Screen:  ${sharedPreference.getSession("selectedPOIDiscovery_XP_Value")}")
        discoveryXP_Point.text="+${sharedPreference.getSession("selectedPOIDiscovery_XP_Value")} XP"
        taskCompleted_XP_Point.text="+${sharedPreference.getSession("selectedPOIChallenge_XP_Value")} XP"

        println("challengeName :: $challengeName")

        if (challengeName == "quiz") {
            challengeIcon.setImageDrawable(getDrawable(R.drawable.quiz_challenge_icon))
            challengeTitle.text = resources.getString(R.string.quiz_challenge)
            questionTwoLabel.text = "Correct Answer"
            subTileOfBeginChallenge.text = "$noOfQuestions"

        } else if (challengeName == "selfie") {
            challengeIcon.setImageDrawable(getDrawable(R.drawable.selfie_challenge_icon))
            challengeTitle.text = resources.getString(R.string.selfie_challenge)
            subTileOfBeginChallenge.text = "Snap a photo at"
            questionTwoLabel.text = "Selfie Posted"
        }

        challenge_backButton.setOnClickListener {
            startActivity(
                Intent(
                    this@ChallengeActivity,
                    DiscoverDetailsScreenActivity::class.java
                ).putExtra("from", resources.getString(R.string.discover)))
            finish()
        }

        beginButton.setOnClickListener {

            try {
                when (challengeName) {
                    "quiz" -> {

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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }



    override fun onBackPressed() {

        startActivity(
            Intent(
                this@ChallengeActivity,
                DiscoverDetailsScreenActivity::class.java
            ).putExtra("from", resources.getString(R.string.discover)))
        finish()
    }


/*    private fun beginChallengeAPI() {
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


                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/



    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }
}