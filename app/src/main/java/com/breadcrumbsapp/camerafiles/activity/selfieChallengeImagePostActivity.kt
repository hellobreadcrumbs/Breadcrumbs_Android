package com.breadcrumbsapp.camerafiles.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.ImageActivityBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.DiscoverScreenActivity
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.image_activity.*
import kotlinx.android.synthetic.main.quiz_challenge.*
import kotlinx.android.synthetic.main.selfie_challenge_level_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// https://github.com/ArthurHub/Android-Image-Cropper
class selfieChallengeImagePostActivity : AppCompatActivity() {
    lateinit var binding: ImageActivityBinding

    private var interceptor = intercept()
    private lateinit var sharedPreference: SessionHandlerClass

    var tempFile:String=""
    private lateinit var dateFormat:String
    private lateinit var poiID:String
    private var scoredValue=0
    private var overallValue=12000
    private var selfiePostValue=50
    private var discoverValue=50
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SessionHandlerClass(applicationContext)

        val uri = sharedPreference.getSession("cameraUri")
        println("ImageUri: $uri")
        val imageUri: Uri = Uri.parse(uri)
        binding.capturedImage.setImageURI(imageUri)

        // start cropping activity for pre-acquired image saved on the device
        CropImage.activity(imageUri).setAspectRatio(1,1)
            .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.FIT_CENTER)
            .setRotationDegrees(0).start(this)


        poiName.text=sharedPreference.getSession("selectedPOIName")

        selfieImagePostBackButton.setOnClickListener {
          //f
            startActivity(
                Intent(
                    this@selfieChallengeImagePostActivity,
                    com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
                )
            )
          //  finish()
        }

        imagePostButton.setOnClickListener {


            if(imagePostButton.text.equals("CONTINUE"))
            {

                Glide.with(applicationContext).load(sharedPreference.getSession("poi_image")).into(selfieChallengeImageView)

                selfieChallengeLevelLayout.visibility=View.VISIBLE
                imagePostLayout.visibility=View.GONE

                selfieChallengeProgressBar.max=overallValue
                scoredValue=discoverValue+selfiePostValue
                selfiePostMark.text= "+$selfiePostValue XP"

                ObjectAnimator.ofInt(selfieChallengeProgressBar, "progress", scoredValue)
                    .setDuration(1000)
                    .start()

                val subtractValue=overallValue-scoredValue
                balanceScoreValue.text="$subtractValue XP to Level 2"
            }
            else
            {
                didU_knowLayout.visibility=View.VISIBLE
                imagePostLayout.visibility=View.VISIBLE
                selfieChallengeLevelLayout.visibility=View.GONE

                selfieImagePostBackButton.visibility=View.INVISIBLE
                titleText.text="Photo posted successfully!"
                did_you_know_txt.visibility=View.VISIBLE
                did_you_know_content.visibility=View.VISIBLE
                imagePostButton.background=getDrawable(R.drawable.selfie_continue_btn)
                imagePostButton.text="CONTINUE"

            }
        }

        selfieChallengeLevelCloseBtn.setOnClickListener {
          /*  startActivity(
                Intent(
                    this@selfieChallengeImagePostActivity,
                    DiscoverScreenActivity::class.java
                ).putExtra("isFromLogin","no")
            )
            overridePendingTransition(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left
            )
            finish()*/
            discoverPOI()
        }


    }
    private fun calculateXPPoints()
    {
        selfieChallengeProgressBar.max=overallValue
        discoverValue= CommonData.getUserDetails!!.experience.toInt()
        scoredValue=discoverValue+selfiePostValue
        selfiePostMark.text= "+$selfiePostValue XP"


        var totalScore=0
        for(i in 0 until CommonData.eventsModelMessage!!.count())
        {
            if(CommonData.eventsModelMessage!![i].disc_id!=null)
            {
                totalScore += CommonData.eventsModelMessage!![i].experience.toInt()
                println("totalScore :: $totalScore")
            }
        }
        println("totalScore :: $totalScore")
        ObjectAnimator.ofInt(selfieChallengeProgressBar, "progress", totalScore)
            .setDuration(1000)
            .start()
        totalScore+=scoredValue
        val subtractValue = overallValue - scoredValue
        balanceScoreValue.text = "$subtractValue XP to Level 2"
    }
    private fun discoverPOI() {


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
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("user_id", sharedPreference.getSession("login_id"))
        jsonObject.put("poi_id", sharedPreference.getSession("selectedPOIID"))

        println("Discover_POI Input = $jsonObject")
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.discoverPOI(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to  JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val registerJSON = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    val jsonElement: JsonElement? = JsonParser.parseString(registerJSON)
                    val jsonObject: JsonObject? = jsonElement?.asJsonObject

                    val status: Boolean = jsonObject?.get("status")!!.asBoolean
                    println("Discover_POI Status = $jsonElement")

                    if (status) {



                        startActivity(
                            Intent(
                                this@selfieChallengeImagePostActivity,
                                DiscoverScreenActivity::class.java
                            ).putExtra("isFromLogin","no")
                        )
                        overridePendingTransition(
                            R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left
                        )
                        finish()
                    }
                } else {

                    println("Printed JSON ELSE : ${response.code()}")

                }
            }
        }
    }
    override fun onBackPressed() {
        //super.onBackPressed()
        if(imagePostButton.text=="POST")
        {
            startActivity(
                Intent(
                    this@selfieChallengeImagePostActivity,
                    com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
                )
            )
        }
        else
        {

            Toast.makeText(applicationContext,"Please click CLOSE button",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                binding.capturedImage.setImageURI(resultUri)
                println("Cropped Image : $resultUri")
                imagePostLayout.visibility=View.VISIBLE

                  poiID=sharedPreference.getSession("selectedPOIID").toString()
                  dateFormat=SimpleDateFormat(FILENAME, Locale.US).format(System.currentTimeMillis())
                  tempFile="uri:${result.uri}"+",type:'image/jpeg',name:'selfie_challenge_'$dateFormat+_$poiID.jpg"
                  println("tempFile $tempFile")
            //    constraintLayout.setBackgroundColor(Color.parseColor("#F8F0DD"))
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

/*
    let _d = new Date();
    let filename = _d.getDate() + "" + _d.getMonth() + "" + _d.getFullYear() + "" + _d.getHours() + "" + _d.getMinutes() + "" + _d.getSeconds()
    var file = {
        uri: $filepath,
        type: 'image/jpeg',
        name: 'selfie_challenge_' + filename + "_" + $poi_id + '.jpg',
    };
*/


    private fun beginSelfieChallenge() {


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
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("user_id", sharedPreference.getSession("login_id").toString())
        jsonObject.put("poi_id", sharedPreference.getSession("selectedPOIID").toString())
        jsonObject.put("file",tempFile)

        println("selfie_challenge Input = $jsonObject")
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.beginSelfieChallenge(resources.getString(R.string.api_access_token),requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to  JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val registerJSON = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    val jsonElement: JsonElement? = JsonParser.parseString(registerJSON)
                    val jsonObject: JsonObject? = jsonElement?.asJsonObject

                    val status: Boolean = jsonObject?.get("status")!!.asBoolean
                    println("selfie_challenge Status = $jsonObject")

                } else {

                    println("Printed JSON ELSE : ${response.code()}")

                }
            }
        }
    }

    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }

    companion object {
        private const val FILENAME = "dd-MM-yyyy-HH-mm-ss"
    }
}