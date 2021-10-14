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
    private lateinit var binding: ImageActivityBinding
    private lateinit var selectedFile: File
    private var interceptor = intercept()
    private lateinit var sharedPreference: SessionHandlerClass
    private var tempFile: String = ""
    private lateinit var dateFormat: String
    private lateinit var dateParam: String
    private lateinit var poiID: String
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
            .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_CROP)
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

        selfieImagePostBackButton.setOnClickListener {

            startActivity(
                Intent(
                    this@SelfieChallengeImagePostActivity,
                    com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
                )
            )

        }

        imagePostButton.setOnClickListener {


            if (imagePostButton.text.equals(resources.getText(R.string.continue_button_text))) {


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

                getUserDetails()

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
            sharedPreference.saveSession("clicked_button", "")
            sharedPreference.saveSession("from_challenge_screen", "YES")
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

        }


    }

    override fun onResume() {
        super.onResume()
        setResult(RESULT_CANCELED)
    }

     private fun calculateUserLevel(exp: Int) {
        var ranking: String = ""
        var level: Int = 0
        var base: Int = 0
        var nextLevel: Int = 0
        when (exp) {
            in 0..999 -> { // 1000 thresh
                ranking = "Recruit"
                level = 1
                base = 1000
                nextLevel = 1000
            }
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
        println("Selfie Challenge => $ranking $level")
        selfie_challenge_level_name.text="$ranking LV. $level"
        val expToLevel = (nextLevel - base) - (exp - base) // (2000-1000) - (400-1000) = (1000)-(-600)=1600
        println("expToLevel= $expToLevel")
        val poiDiscoverXP: Int = sharedPreference.getSession("selectedPOIDiscovery_XP_Value")!!.toInt()
        val poiChallengeXP: Int = sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()
        //   val totalXP: Int = poiDiscoverXP + poiChallengeXP + exp
        //   println("Report => $poiDiscoverXP + $poiChallengeXP + $exp = $totalXP")
        var balanceVal: Int = nextLevel - exp
        println("Report => $nextLevel - $exp  = $balanceVal")
        //Report => 3000 - 2000  = 1000

        selfieChallengeProgressBar.max = nextLevel
        if(balanceVal<=0)
        {
            val levelValue=level + 1
            selfie_challenge_level_name.text="$ranking LV. $levelValue"

            println("Report => IF = $nextLevel - $exp  = $balanceVal")
            balanceScoreValue.text = " $exp XP TO LV. ${level + 1}"

            ObjectAnimator.ofInt(
                selfieChallengeProgressBar,
                "progress",
                balanceVal
            )
                .setDuration(1000)
                .start()

        }
        else{
            println("Report => ELSE = $nextLevel - $exp  = $balanceVal")

            balanceScoreValue.text = " $balanceVal XP TO LV. ${level + 1}"

            ObjectAnimator.ofInt(
                selfieChallengeProgressBar,
                "progress",
                exp
            )
                .setDuration(1000)
                .start()
        }


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
                   // val jsonElement: JsonElement? = JsonParser.parseString(registerJSON)
                   // val jsonObject: JsonObject? = jsonElement?.asJsonObject

                   // val status: Boolean = jsonObject?.get("status")!!.asBoolean
                  //  println("Discover_POI Status = $jsonElement")


                } else {

                    println("Printed JSON ELSE : ${response.code()}")

                }
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        if (imagePostButton.text == resources.getText(R.string.post_button_text)) {
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
            println("Crop:: Result => $resultCode :: $result")
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                binding.capturedImage.setImageURI(resultUri)

                imagePostLayout.visibility = View.VISIBLE

                poiID = sharedPreference.getSession("selectedPOIID").toString()
                val c = Calendar.getInstance()
             //   dateFormat = SimpleDateFormat(FILENAME, Locale.US).format(System.currentTimeMillis())
                val month =c.get(Calendar.MONTH)
                dateFormat="${c.get(Calendar.DAY_OF_MONTH)}${month+1}${c.get(Calendar.YEAR)}${c.get(Calendar.HOUR_OF_DAY)}${c.get(Calendar.MINUTE)}${c.get(Calendar.SECOND)}"
                tempFile =
                    "uri:${result.uri}" + ",type:'image/jpeg',name:'selfie_challenge_'$dateFormat+_$poiID.jpg"
                println("tempFile $dateFormat")



                  dateParam="${c.get(Calendar.YEAR)}-${month+1}-${c.get(Calendar.DAY_OF_MONTH)} ${c.get(Calendar.HOUR_OF_DAY)}:${c.get(
                    Calendar.MINUTE)}:${c.get(Calendar.SECOND)}"

                println("dateParam= $dateParam")


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


                println("Selfie :: 2 ${selectedFile.name}")

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error

            }
            else
            {
                val uri = sharedPreference.getSession("cameraUri")
                val imageUri: Uri = Uri.parse(uri)
                CropImage.activity(imageUri).setAspectRatio(1, 1)
                    .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_CROP)
                    .start(this)
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

            val matrix = Matrix()
            when (orientation) {
                6 -> {
                    matrix.postRotate(90F)

                }
                3 -> {
                    matrix.postRotate(180F)

                }
                8 -> {
                    matrix.postRotate(270F)

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
        val c = Calendar.getInstance()

        val month =c.get(Calendar.MONTH)
        dateFormat="${c.get(Calendar.DAY_OF_MONTH)}${month+1}${c.get(Calendar.YEAR)}${c.get(Calendar.HOUR)}${c.get(Calendar.MINUTE)}${c.get(Calendar.SECOND)}"
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "selfie_pic_$dateFormat"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun updateFile(loginID: String, poiID: String) {

        println("Selfie tempFile == $tempFile")
        println("Selfie login == $loginID")
        println("Selfie poiID == $poiID")
        println("Selfie dateParam == $dateParam")

        val id: RequestBody =
            loginID.toRequestBody(contentType = "text/plain".toMediaTypeOrNull())
        val poiId: RequestBody =
            poiID.toRequestBody(contentType = "text/plain".toMediaTypeOrNull())
        val reqFile: RequestBody =
            selectedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())//RequestBody.create( selectedFile, MediaType.parse("image/*"))
        val multiPartFile = MultipartBody.Part.createFormData(
            "file", tempFile,
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
                        titleText.text = resources.getText(R.string.image_posted_successfully)
                         imagePostButton.background = getDrawable(R.drawable.selfie_continue_btn)
                        imagePostButton.text = resources.getText(R.string.continue_button_text)

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
        private const val FILENAME = "dd-MM-yyyy-hh-mm-ss"
    }
}