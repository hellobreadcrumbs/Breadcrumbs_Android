package com.breadcrumbsapp.view.arcore

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
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.ArImagePostLayoutBinding
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
import kotlinx.android.synthetic.main.ar_challenge_layout.*
import kotlinx.android.synthetic.main.ar_image_post_layout.*
import kotlinx.android.synthetic.main.image_activity.*
import kotlinx.android.synthetic.main.image_activity.imagePostButton
import kotlinx.android.synthetic.main.image_activity.imagePostLayout
import kotlinx.android.synthetic.main.quiz_challenge.*
import kotlinx.android.synthetic.main.selfie_challenge_level_layout.*
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

class ARImagePostScreen : AppCompatActivity() {
    private lateinit var sharedPreference: SessionHandlerClass
    lateinit var binding: ArImagePostLayoutBinding
    private var selfiePostValue = 50
    private var scoredValue = 0
    private var overallValue = 12000
    private var discoverValue = 1000
    private var interceptor = intercept()
    lateinit var selectedFile: File
    private var selectedTrailID: String = ""
    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon

    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArImagePostLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SessionHandlerClass(applicationContext)
        val uri = sharedPreference.getSession("arURI")
        println("ImageUri: $uri")
        val imageUri: Uri = Uri.parse(uri)
        binding.capturedImage.setImageURI(imageUri)

        arPoiTitle.text = sharedPreference.getSession("selectedPOIName")
        CropImage.activity(imageUri).setAspectRatio(1, 1).setFixAspectRatio(true).start(this)


        selectedTrailID = sharedPreference.getSession("selected_trail_id").toString()


        if(sharedPreference.getSession("selectedPOITrivia")!="")
        {
            binding.didYouKnowTxt.visibility=View.VISIBLE
            binding.didYouKnowContent.visibility=View.VISIBLE

            binding.didYouKnowContent.text=sharedPreference.getSession("selectedPOITrivia")


        }
        else
        {
            binding.didYouKnowTxt.visibility=View.GONE
            binding.didYouKnowContent.visibility=View.GONE
        }


        for(i in CommonData.getTrailsData!!.indices)
        {
            if(CommonData.getTrailsData!![i].id==selectedTrailID)
            {
                println("Details IF ::: Trail ID = ${CommonData.getTrailsData!![i].id} Completed_POI == ${CommonData.getTrailsData!![i].completed_poi_count}")

                val updatedPoiCount= CommonData.getTrailsData!![i].completed_poi_count.toInt()+1
                println("updatedPoiCount = $updatedPoiCount")
                ar_challenge_screen_poi_completed_details.text="$updatedPoiCount /" +
                        " ${CommonData.getTrailsData!![i].poi_count} POIs DISCOVERED"


                val localImagePath=resources.getString(R.string.staging_url)+CommonData.getTrailsData!![i].map_icon_dt_url
                Glide.with(applicationContext).load(localImagePath).into(ar_challenge_screen_trail_icon)
                Glide.with(applicationContext).load(localImagePath).into(ar_image_post_banner_trail_image)
                ar_image_post_banner_trail_name.text=CommonData.getTrailsData!![i].name





            }
        }
       /* if(selectedTrailID=="4")
        {
            Glide.with(applicationContext).load(trailIcons[1]).into(ar_challenge_screen_trail_icon)
        }
        else if(selectedTrailID=="6")
        {
            Glide.with(applicationContext).load(trailIcons[2]).into(ar_challenge_screen_trail_icon)
        }*/


        arChallengeLevelCloseBtn.setOnClickListener {

            discoverPOI()
        }
        binding.arImagePostBackButton.setOnClickListener {
            //    CropImage.activity(imageUri).setAspectRatio(1, 1).setFixAspectRatio(true).start(this)

            startActivity(Intent(this@ARImagePostScreen, ARCoreActivity::class.java))
        }
        binding.imagePostButton.setOnClickListener {


            if (imagePostButton.text.equals("CONTINUE")) {


                println("AR IMAGE :: ${sharedPreference.getSession("selectedPOIImage")}")

                Glide.with(applicationContext)
                    .load(sharedPreference.getSession("selectedPOIImage"))
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            ar_challenge_screen_loader.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            ar_challenge_screen_loader.visibility = View.GONE
                            return false
                        }
                    })
                    .into(arChallengeImageView)

                binding.imagePostLayout.visibility = View.GONE
                binding.arSelfieChallengeLevelLayout.visibility = View.VISIBLE
                imagePostLayout.visibility = View.GONE

                /*       selfieChallengeProgressBar.max = overallValue
                       scoredValue = discoverValue + selfiePostValue
                       arSelfiePostMark.text = "+$selfiePostValue XP"

                       ObjectAnimator.ofInt(selfieChallengeProgressBar, "progress", scoredValue)
                           .setDuration(1000)
                           .start()

                       val subtractValue = overallValue - scoredValue
                       arBalanceScoreValue.text = "$subtractValue XP to Level 2"*/

                calculateXPPoints()
            } else {
                binding.didUKnowLayout.visibility = View.VISIBLE
                binding.imagePostLayout.visibility = View.VISIBLE
                binding.arSelfieChallengeLevelLayout.visibility = View.GONE

                binding.arImagePostBackButton.visibility = View.INVISIBLE


                try {

                    val userID = sharedPreference.getSession("login_id") as String
                    val poiID = sharedPreference.getSession("selectedPOIID") as String
                    updateFile(userID, poiID)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                /*binding.titleText.text = "Photo posted successfully!"
                binding.didYouKnowTxt.visibility = View.VISIBLE
                binding.didYouKnowContent.visibility = View.VISIBLE
                imagePostButton.background = getDrawable(R.drawable.selfie_continue_btn)
                imagePostButton.text = "CONTINUE"*/
            }

        }

    }

    private fun calculateXPPoints() {
        try {
            /*arSelfieChallengeProgressBar.max = overallValue
            discoverValue = CommonData.getUserDetails!!.experience.toInt()
            println("discover_value $discoverValue")
            scoredValue = discoverValue + selfiePostValue
            arSelfiePostMark.text = "+$selfiePostValue XP"

            var totalScore = 0
            for (i in 0 until CommonData.eventsModelMessage!!.count()) {
                if (CommonData.eventsModelMessage!![i].disc_id != null) {
                    totalScore += CommonData.eventsModelMessage!![i].experience.toInt()
                    println("totalScore :: $totalScore")
                }
            }
            println("totalScore :: $totalScore")
            ObjectAnimator.ofInt(arSelfieChallengeProgressBar, "progress", totalScore)
                .setDuration(1000)
                .start()

            totalScore += scoredValue
            val subtractValue = overallValue - totalScore
            arBalanceScoreValue.text = "$subtractValue XP to Level 2"

*/


            // Updated One with API data..
/*

            val progressBarMaxValue = sharedPreference.getIntegerSession("xp_point_nextLevel_value")
            val expToLevel = sharedPreference.getIntegerSession("expTo_level_value")
            val completedPoints = sharedPreference.getSession("player_experience_points")
            val levelValue = sharedPreference.getSession("lv_value")
            val presentLevel = sharedPreference.getSession("current_level")
            scoredValue = discoverValue + selfiePostValue
            arSelfiePostMark.text = "+$selfiePostValue XP"
            arSelfieChallengeProgressBar.max = progressBarMaxValue
            arBalanceScoreValue.text = "$expToLevel XP TO $levelValue"
            ar_challenge_level_name.text=presentLevel
            ObjectAnimator.ofInt(arSelfieChallengeProgressBar, "progress", completedPoints!!.toInt())
                .setDuration(1000)
                .start()
*/





            println("AR XP Details : progressBarMaxValue = calculate points")
            var progressBarMaxValue = sharedPreference.getIntegerSession("xp_point_nextLevel_value")
            val expToLevel = sharedPreference.getIntegerSession("expTo_level_value")
            val completedPoints = sharedPreference.getSession("player_experience_points")
            var levelValue = sharedPreference.getSession("lv_value")
            val presentLevel = sharedPreference.getSession("current_level")


            println("AR XP Details : progressBarMaxValue = $progressBarMaxValue")
            println("AR XP Details : completedPoints = $completedPoints")
            println("AR XP Details : presentLevel = $presentLevel")
            println("AR XP Details : levelValue = $levelValue")
            println("AR XP Details : expToLevel = $expToLevel")




            val POIDiscoverXP:Int=sharedPreference.getSession("selectedPOIDiscovery_XP_Value")!!.toInt()
            val POIchallengeXP:Int=sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()
            val completedPointIntValue=completedPoints!!.toInt()
            println("AR XP Details : POIDiscoverXP = $POIDiscoverXP")
            println("AR XP Details : POIchallengeXP = $POIchallengeXP")

            val totalXP:Int=POIDiscoverXP+POIchallengeXP+completedPointIntValue
            println("AR XP Details : totalXP = $totalXP")

            arDiscoveryMark.text="+${sharedPreference.getSession("selectedPOIDiscovery_XP_Value")} XP"
            arSelfiePostMark.text="+${sharedPreference.getSession("selectedPOIChallenge_XP_Value")} XP"
            //scoredValue = discoverValue + selfiePostValue
           // selfiePostMark.text = "+$selfiePostValue XP"
            ar_challenge_level_name.text=presentLevel

            /*val totalGainedXP:Int=sharedPreference.getSession("selectedPOIDiscovery_XP_Value")!!.toInt()
            +sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()+expToLevel.toInt()
    */
            println("AR XP Details : totalGainedXP = $totalXP")

            var balanceVal:Int=progressBarMaxValue-totalXP
            if(balanceVal<0)
            {
                progressBarMaxValue += 2000
                balanceVal=progressBarMaxValue-totalXP
                levelValue="LV ${sharedPreference.getIntegerSession("current_level")}"
            }

            arBalanceScoreValue.text = "$balanceVal XP TO $levelValue"

            sharedPreference.saveSession("xp_balance_value",balanceVal)
            sharedPreference.saveSession("total_gained_xp",totalXP)
            sharedPreference.saveSession("balance_xp_string",arBalanceScoreValue.text.toString())

            arSelfieChallengeProgressBar.max = progressBarMaxValue
            ObjectAnimator.ofInt(arSelfieChallengeProgressBar, "progress", totalXP)
                .setDuration(1000)
                .start()
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


                        startActivity(
                            Intent(
                                this@ARImagePostScreen,
                                DiscoverScreenActivity::class.java
                            ).putExtra("isFromLogin", "no")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                binding.capturedImage.setImageURI(resultUri)
                println("Cropped Image : $resultUri")
                imagePostLayout.visibility = View.VISIBLE

                /*   poiID=sharedPreference.getSession("selectedPOIID").toString()
                   dateFormat= SimpleDateFormat(selfieChallengeImagePostActivity.FILENAME, Locale.US).format(System.currentTimeMillis())
                   tempFile="uri:${result.uri}"+",type:'image/jpeg',name:'selfie_challenge_'$dateFormat+_$poiID.jpg"
                   println("tempFile $tempFile")*/
                //    constraintLayout.setBackgroundColor(Color.parseColor("#F8F0DD"))


                val f = File(resultUri!!.path)
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

                println("AR :: 1  $selectedFile")
                println("AR :: 2 ${selectedFile.name}")
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
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
        val imageFileName = "ar_image_$timeStamp"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun updateFile(loginID: String, poiID: String) {

        println("AR selectedFile.name == ${selectedFile.name}")
        println("AR login == $loginID")
        println("AR poiID == $poiID")

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
                    println("AR Image JSon Body if  ${response.body()}")
                    runOnUiThread {
                        binding.titleText.text = "Photo posted successfully!"


                        imagePostButton.background = getDrawable(R.drawable.selfie_continue_btn)
                        imagePostButton.text = "CONTINUE"
                    }

                } else {
                    println("AR Image JSon Body else  ${response.body()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(applicationContext,"Please try again...",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@ARImagePostScreen, ARCoreActivity::class.java))
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