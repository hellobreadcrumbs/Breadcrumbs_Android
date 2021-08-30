package com.breadcrumbsapp.camerafiles.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import com.breadcrumbsapp.util.FilePathUtils
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
import kotlinx.android.synthetic.main.profile_edit_layout.*
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

                Glide.with(applicationContext).load(sharedPreference.getSession("poi_image"))
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

                calculateXPPoints()
            } else {
                didU_knowLayout.visibility = View.VISIBLE
                imagePostLayout.visibility = View.VISIBLE
                selfieChallengeLevelLayout.visibility = View.GONE
                selfieImagePostBackButton.visibility = View.INVISIBLE



                try {

                    val userID = sharedPreference.getSession("login_id") as String
                    val poiID = sharedPreference.getSession("selectedPOIID") as String
                    updateFile(userID, poiID)
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                /*       titleText.text="Photo posted successfully!"
                       did_you_know_txt.visibility=View.VISIBLE
                       did_you_know_content.visibility=View.VISIBLE
                       imagePostButton.background=getDrawable(R.drawable.selfie_continue_btn)
                       imagePostButton.text="CONTINUE"*/

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


        var progressBarMaxValue = sharedPreference.getIntegerSession("xp_point_nextLevel_value")
        var expToLevel = sharedPreference.getIntegerSession("expTo_level_value")
        var completedPoints = sharedPreference.getSession("player_experience_points")
        val levelValue = sharedPreference.getSession("lv_value")
        scoredValue = discoverValue + selfiePostValue
        selfiePostMark.text = "+$selfiePostValue XP"
        selfieChallengeProgressBar.max = progressBarMaxValue
        balanceScoreValue.text = "$expToLevel XP TO $levelValue"
        ObjectAnimator.ofInt(selfieChallengeProgressBar, "progress", completedPoints!!.toInt())
            .setDuration(1000)
            .start()
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
            .baseUrl(resources.getString(R.string.live_url))
            .client(okHttpClient)
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("user_id", sharedPreference.getSession("login_id").toString())
        jsonObject.put("poi_id", sharedPreference.getSession("selectedPOIID").toString())
        jsonObject.put("file", tempFile)

        println("selfie_challenge Input = $jsonObject")
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.beginSelfieChallenge(
                resources.getString(R.string.api_access_token),
                requestBody
            )

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

    private fun getRealPathFromURI(uri: Uri?): String? {
        var path = ""
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
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
                        titleText.text="Photo posted successfully!"
                        did_you_know_txt.visibility=View.VISIBLE
                        did_you_know_content.visibility=View.VISIBLE
                        imagePostButton.background=getDrawable(R.drawable.selfie_continue_btn)
                        imagePostButton.text="CONTINUE"

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