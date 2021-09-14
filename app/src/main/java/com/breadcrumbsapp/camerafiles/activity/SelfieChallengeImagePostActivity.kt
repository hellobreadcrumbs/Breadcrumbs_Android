package com.breadcrumbsapp.camerafiles.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.challenge_activity.*
import kotlinx.android.synthetic.main.image_activity.*
import kotlinx.android.synthetic.main.profile_edit_layout.*
import kotlinx.android.synthetic.main.quiz_challenge.*
import kotlinx.android.synthetic.main.selfie_challenge_level_layout.*
import kotlinx.android.synthetic.main.user_profile_screen_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// https://github.com/ArthurHub/Android-Image-Cropper
class SelfieChallengeImagePostActivity : AppCompatActivity() {
    lateinit var binding: ImageActivityBinding
    lateinit var selectedFile: File
    private var interceptor = intercept()
    private lateinit var sharedPreference: SessionHandlerClass

    var tempFile: String = ""
    private lateinit var dateFormat: String
    private lateinit var poiID: String
    private var scoredValue = 0
    private var overallValue = 12000
    private var selfiePostValue = 50
    private var discoverValue = 50
    private var selectedTrailID: String = ""

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
        CropImage.activity(imageUri).setAspectRatio(1, 1)
            .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.FIT_CENTER)
            .start(this)


        poiName.text = sharedPreference.getSession("selectedPOIName")
        selectedTrailID = sharedPreference.getSession("selected_trail_id").toString()


        if (sharedPreference.getSession("selectedPOITrivia") != "") {
            did_you_know_txt.visibility = View.VISIBLE
            did_you_know_content.visibility = View.VISIBLE

            did_you_know_content.text = sharedPreference.getSession("selectedPOITrivia")

        } else {
            did_you_know_txt.visibility = View.GONE
            did_you_know_content.visibility = View.GONE
        }


        for (i in CommonData.getTrailsData!!.indices) {
            if (CommonData.getTrailsData!![i].id == selectedTrailID) {
                println("Details IF ::: Trail ID = ${CommonData.getTrailsData!![i].id} Completed_POI == ${CommonData.getTrailsData!![i].completed_poi_count}")

                val updatedPoiCount = CommonData.getTrailsData!![i].completed_poi_count.toInt() + 1
                println("updatedPoiCount = $updatedPoiCount")
                selfie_challenge_screen_poi_completed_details.text = "$updatedPoiCount /" +
                        " ${CommonData.getTrailsData!![i].poi_count} POIs DISCOVERED"

                val localImagePath =
                    resources.getString(R.string.staging_url) + CommonData.getTrailsData!![i].map_icon_dt_url
                Glide.with(applicationContext).load(localImagePath)
                    .into(selfie_image_post_banner_trail_image)
                Glide.with(applicationContext).load(localImagePath)
                    .into(selfie_challenge_screen_trail_icon)

            }
        }

        //selectedPOIName
        selfie_image_post_banner_trail_name.text =
            "${sharedPreference.getSession("selectedPOIName")}"

        /*    for(i in CommonData.getTrailsData!!.indices)
            {
                if(CommonData.getTrailsData!![i].id==selectedTrailID)
                {
                    val localImagePath=resources.getString(R.string.staging_url)+CommonData.getTrailsData!![i].map_icon_dt_url
                    Glide.with(applicationContext).load(localImagePath).into(selfie_image_post_banner_trail_image)
                    Glide.with(applicationContext).load(localImagePath).into(selfie_challenge_screen_trail_icon)
                }
            }*/

        selfieImagePostBackButton.setOnClickListener {
            //f
            startActivity(
                Intent(
                    this@SelfieChallengeImagePostActivity,
                    com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
                )
            )
            //  finish()
        }

        imagePostButton.setOnClickListener {


            if (imagePostButton.text.equals("CONTINUE")) {

                /*Glide.with(applicationContext).load(sharedPreference.getSession("poi_image"))
                    .into(selfieChallengeImageView)*/
                println("SELFIE IMAGE :: ${sharedPreference.getSession("poi_image")}")
                Glide.with(applicationContext)
                    .load(sharedPreference.getSession("poi_image"))
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            selfie_challenge_screen_loader.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            selfie_challenge_screen_loader.visibility = View.GONE
                            return false
                        }
                    })
                    .into(selfieChallengeImageView)

                selfieChallengeLevelLayout.visibility = View.VISIBLE
                imagePostLayout.visibility = View.GONE

                /* selfieChallengeProgressBar.max=overallValue
                 scoredValue=discoverValue+selfiePostValue
                 selfiePostMark.text= "+$selfiePostValue XP"

                 ObjectAnimator.ofInt(selfieChallengeProgressBar, "progress", scoredValue)
                     .setDuration(1000)
                     .start()

                 val subtractValue=overallValue-scoredValue
                 balanceScoreValue.text="$subtractValue XP to Level 2"*/

                getUserDetails()
                //calculateXPPoints()
            } else {
                didU_knowLayout.visibility = View.VISIBLE
                imagePostLayout.visibility = View.VISIBLE
                selfieChallengeLevelLayout.visibility = View.GONE
                selfieImagePostBackButton.visibility = View.INVISIBLE


                println("imagePostButton ELSE AREA...........")

                try {

                    val userID = sharedPreference.getSession("login_id") as String
                    val poiID = sharedPreference.getSession("selectedPOIID") as String
                    updateFile(userID, poiID)
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }

        selfieChallengeLevelCloseBtn.setOnClickListener {

            startActivity(
                Intent(
                    this@SelfieChallengeImagePostActivity,
                    DiscoverScreenActivity::class.java
                ).putExtra("isFromLogin", "no")
            )
            overridePendingTransition(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left
            )
            finish()
            //  discoverPOI()
        }


    }

    private fun calculateXPPoints() {
        /*  selfieChallengeProgressBar.max=overallValue
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
          balanceScoreValue.text = "$subtractValue XP to Level 2"*/


        println("Selfie XP Details : progressBarMaxValue = calculate points")
        var progressBarMaxValue = sharedPreference.getIntegerSession("xp_point_nextLevel_value")
        val expToLevel = sharedPreference.getIntegerSession("expTo_level_value")
        val previousPoints = sharedPreference.getSession("player_experience_points")
        var levelValue = sharedPreference.getSession("lv_value")
        val presentLevel = sharedPreference.getSession("current_level")


        println("Selfie XP Details : progressBarMaxValue = $progressBarMaxValue")
        println("Selfie XP Details : previousPoints = $previousPoints")
        println("Selfie XP Details : presentLevel = $presentLevel")
        println("Selfie XP Details : levelValue = $levelValue")
        println("Selfie XP Details : expToLevel = $expToLevel")


        val POIDiscoverXP: Int =
            sharedPreference.getSession("selectedPOIDiscovery_XP_Value")!!.toInt()
        val POIchallengeXP: Int =
            sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()
        val completedPointIntValue = previousPoints!!.toInt()
        println("Selfie XP Details : POIDiscoverXP = $POIDiscoverXP")
        println("Selfie XP Details : POIchallengeXP = $POIchallengeXP")

        val totalXP: Int = POIDiscoverXP + POIchallengeXP + completedPointIntValue
        println("Selfie XP Details : totalXP = $totalXP")

        selfieDiscoveryMark.text =
            "+${sharedPreference.getSession("selectedPOIDiscovery_XP_Value")} XP"
        selfiePostMark.text = "+${sharedPreference.getSession("selectedPOIChallenge_XP_Value")} XP"

        selfie_challenge_level_name.text = presentLevel

        println("Selfie XP Details : totalGainedXP = $totalXP")

        var balanceVal: Int = progressBarMaxValue - totalXP
        if (balanceVal < 0) {
            progressBarMaxValue += 2000
            balanceVal = progressBarMaxValue - totalXP
            levelValue = "LV ${sharedPreference.getSession("current_level") as Int}"
        }

        balanceScoreValue.text = "$balanceVal XP TO $levelValue"

        sharedPreference.saveSession("xp_balance_value", balanceVal)
        sharedPreference.saveSession("total_gained_xp", totalXP)
        sharedPreference.saveSession("balance_xp_string", balanceScoreValue.text.toString())


        println("Selfie XP Details : levelValue = $levelValue")
        println("Selfie XP Details : balanceVal = $balanceVal")
        println("Selfie XP Details : balanceScoreValue.text = ${balanceScoreValue.text}")

        selfieChallengeProgressBar.max = progressBarMaxValue
        ObjectAnimator.ofInt(selfieChallengeProgressBar, "progress", totalXP)
            .setDuration(1000)
            .start()
    }
    private fun calculateUserLevel(exp: Int) {
        var ranking: String = ""
        var level: Int = 0
        var base: Int = 0
        var nextLevel: Int = 0
        when (exp) {
            in 0..999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 1
                base = 1000
                nextLevel = 2000
            }
            in 1000..1999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 2
                base = 1000
                nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 3
                base = 2000
                nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 4
                base = 3000
                nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 5
                base = 4000
                nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 6
                base = 6000
                nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 7
                base = 8000
                nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 8
                base = 10000
                nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 9
                base = 12000
                nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "NAVIGATOR"
                level = 10
                base = 14000
                nextLevel = 17000

            }
            in 17000..20499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 11
                base = 17000
                nextLevel = 20500

            }
            in 20500..24499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 12
                base = 20500
                nextLevel = 24500

            }
            in 24500..28499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 13
                base = 24500
                nextLevel = 28500

            }
            in 28500..33499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 14
                base = 28500
                nextLevel = 33500

            }
            in 33500..38999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 15
                base = 33500
                nextLevel = 39000

            }
            in 39000..44999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 16
                base = 39000
                nextLevel = 45000

            }
            in 45000..51499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 17
                base = 45000
                nextLevel = 51500

            }
            in 51500..58499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 18
                base = 51500
                nextLevel = 58500

            }
            in 58500..65999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 19
                base = 58500
                nextLevel = 66000

            }
            in 66000..73999 -> { // 2000 thresh
                ranking = "Captain"
                level = 20
                base = 66000
                nextLevel = 74000

            }
        }
        println("Selfie Challenge => $ranking Lv. $level")
        val expToLevel = (nextLevel - base) - (exp - base) // (2000-1000) - (400-1000) = (1000)-(-600)=1600
        println("expToLevel= $expToLevel")
        val poiDiscoverXP: Int = sharedPreference.getSession("selectedPOIDiscovery_XP_Value")!!.toInt()
        val poiChallengeXP: Int = sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()
        //   val totalXP: Int = poiDiscoverXP + poiChallengeXP + exp
        //   println("Report => $poiDiscoverXP + $poiChallengeXP + $exp = $totalXP")
        val balanceVal: Int = nextLevel - exp
        println("Report => $nextLevel - $exp  = $balanceVal")
        balanceScoreValue.text = " $balanceVal XP TO Lv ${level + 1}"

        selfieChallengeProgressBar.max = nextLevel
        ObjectAnimator.ofInt(
            selfieChallengeProgressBar,
            "progress",
            exp
        )
            .setDuration(1000)
            .start()


        selfie_challenge_level_name.text="$ranking $level"
        selfieDiscoveryMark.text = "+${sharedPreference.getSession("selectedPOIDiscovery_XP_Value")} XP"
        selfiePostMark.text = "+${sharedPreference.getSession("selectedPOIChallenge_XP_Value")} XP"



    }
    private fun getUserDetails() {

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


            println("getUserDetails Url = ${resources.getString(R.string.staging_url)}")
            println("getUserDetails Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getUserDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )


                if (response.isSuccessful) {
                    if (response.body()!!.status) {
                        if (response.body()!!.message != null) {

                            CommonData.getUserDetails = response.body()?.message

                            println("GetUseDetails = ${CommonData.getUserDetails!!.experience}")


                            runOnUiThread {
                                println("From Get User :: ${Integer.parseInt(CommonData.getUserDetails!!.experience)}")
                                calculateUserLevel(Integer.parseInt(CommonData.getUserDetails!!.experience))
                            }


                        } else {

                        }
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

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


                    }
                } else {

                    println("Printed JSON ELSE : ${response.code()}")

                }
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (imagePostButton.text == "POST") {
            startActivity(
                Intent(
                    this@SelfieChallengeImagePostActivity,
                    com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
                )
            )
        } else {

            Toast.makeText(applicationContext, "Please click CLOSE button", Toast.LENGTH_SHORT)
                .show()
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
                imagePostLayout.visibility = View.VISIBLE

                poiID = sharedPreference.getSession("selectedPOIID").toString()
                dateFormat =
                    SimpleDateFormat(FILENAME, Locale.US).format(System.currentTimeMillis())
                tempFile =
                    "uri:${result.uri}" + ",type:'image/jpeg',name:'selfie_challenge_'$dateFormat+_$poiID.jpg"
                println("tempFile $tempFile")
                //    constraintLayout.setBackgroundColor(Color.parseColor("#F8F0DD"))

                println("Result_Pth = ${resultUri!!.path}")


                //try {
                // val path = FilePathUtils.passUri(data, applicationContext)

                // if (path != null) {
                val f = File(resultUri.path)
                val newFile = createImageFile()
                val bitmap = BitmapFactory.decodeFile(f.path)
                val newBitMap =
                    setOrientationForImage(bitmap, f.path)
                newBitMap?.compress(
                    Bitmap.CompressFormat.JPEG,
                    40,
                    FileOutputStream(newFile)
                )

                selectedFile = f

                println("Selfie :: 1  $selectedFile")
                println("Selfie :: 2 ${selectedFile.name}")
                // }
                /* } catch (e: Exception) {
                     e.printStackTrace()
                 }*/
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }


    private fun setOrientationForImage(scaledBitmap: Bitmap, filePath: String): Bitmap? {
        val exif: ExifInterface
        var newBitMap = scaledBitmap
        try {
            exif = ExifInterface(filePath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            when (orientation) {
                6 -> {
                    matrix.postRotate(90F)
                    Log.d("EXIF", "Exif: $orientation")
                }
                3 -> {
                    matrix.postRotate(180F)
                    Log.d("EXIF", "Exif: $orientation")
                }
                8 -> {
                    matrix.postRotate(270F)
                    Log.d("EXIF", "Exif: $orientation")
                }
            }

            newBitMap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
            return newBitMap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun createImageFile(): File? {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "selfie_pic_$timeStamp"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun updateFile(loginID: String, poiID: String) {

        println("Selfie selectedFile.name == ${selectedFile.name}")
        println("Selfie login == $loginID")
        println("Selfie poiID == $poiID")

        val id: RequestBody =
            loginID.toRequestBody(contentType = "text/plain".toMediaTypeOrNull())
        val poiId: RequestBody =
            poiID.toRequestBody(contentType = "text/plain".toMediaTypeOrNull())
        val reqFile: RequestBody =
            selectedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())//RequestBody.create( selectedFile, MediaType.parse("image/*"))
        val multiPartFile = MultipartBody.Part.createFormData(
            "file", selectedFile.name,
            reqFile
        )


        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(intercept())
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.staging_url))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(APIService::class.java)


        CoroutineScope(Dispatchers.Main).launch {
            try {

                val response = apiService.uploadSelfieImage(
                    resources.getString(R.string.api_access_token),
                    id, poiId, multiPartFile
                )


                if (response.isSuccessful) {
                    println("Selfie Image JSon Body if  ${response.body()}")

                    runOnUiThread {
                        titleText.text = "Photo posted successfully!"


                        imagePostButton.background = getDrawable(R.drawable.selfie_continue_btn)
                        imagePostButton.text = "CONTINUE"

                        discoverPOI()

                    }
                } else {
                    println("Selfie Image JSon Body else  ${response.body()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
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