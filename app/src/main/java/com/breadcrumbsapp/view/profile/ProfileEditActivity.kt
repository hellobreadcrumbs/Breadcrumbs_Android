package com.breadcrumbsapp.view.profile

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.ProfileEditLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.FilePathUtils
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.DiscoverScreenActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile_edit_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileEditActivity : AppCompatActivity() {
    private lateinit var sharedPreference: SessionHandlerClass
    private lateinit var binding: ProfileEditLayoutBinding

    val REQUEST_FILE_CHOOSE = 501
    val REQUEST_TAKE_PHOTO = 502
    val PERMISSION_REQUEST_CODE = 505
    val REQUEST_STORAGE_PERMISSION = 505

    var currentRequestFor = 0
    lateinit var currentPhotoPath: String
    lateinit var loginID: String
    lateinit var selectedFile: File
    lateinit var selectedFileName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileEditLayoutBinding.inflate(layoutInflater)
        sharedPreference = SessionHandlerClass(applicationContext)
        setContentView(binding.root)
       // nick_name_text.text = sharedPreference.getSession("player_name")
        nick_name_text.text = CommonData.getUserDetails!!.username
        loginID=sharedPreference.getSession("login_id") as String



        val localProfilePic =
            resources.getString(R.string.staging_url) +sharedPreference.getSession("player_photo_url")

            Glide.with(applicationContext).load(localProfilePic).placeholder(R.drawable.no_image)
                .into(profile_edit_screen_profile_pic_iv)



        profile_edit_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()

        })

        profile_edit_screen_profile_pic_iv.setOnClickListener(View.OnClickListener {

            choosePhoto()
        })




    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "profile_pic_$timeStamp"
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            imageFileName,
            null
        )
        return Uri.parse(path)
    }

    private fun choosePhoto() {
        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.edit_profile_photo_choose_layout)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setDimAmount(0.8f)


        val cameraButton = dialog.findViewById(R.id.tv_camera) as TextView
        val galleryButton = dialog.findViewById(R.id.tv_gallery) as TextView
        val popCloseButton = dialog.findViewById(R.id.pop_up_close_btn) as ImageView

        cameraButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

            dispatchTakePictureIntent(REQUEST_TAKE_PHOTO)

        })

        galleryButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()

            showFileChooser(REQUEST_FILE_CHOOSE)


        })

        popCloseButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })


        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
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

    private fun dispatchTakePictureIntent(requestCode: Int) {


        if (checkRunTimePermission()) {
            callTakePhoto(requestCode)
        } else {
            currentRequestFor = requestCode
            requestPermission()
        }


    }

    private fun redirectToGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestCode)
    }

    private fun showFileChooser(requestCode: Int) {

        if (checkStoragePermission()) {
            redirectToGallery(requestCode)
        } else {

            requestStoragePermission()
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

    @Suppress("DEPRECATION")
    private fun callTakePhoto(requestCode: Int) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, requestCode)


    }


    private fun createImageFile(): File? {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "profile_pic_$timeStamp"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        }
    }

    private fun requestStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, REQUEST_STORAGE_PERMISSION)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, REQUEST_STORAGE_PERMISSION)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this@ProfileEditActivity,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        }
    }

    private fun checkRunTimePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] === PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted) {
                        if (checkStoragePermission()) {
                            dispatchTakePictureIntent(currentRequestFor)
                        } else {
                            requestStoragePermission()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.give_permission),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.give_permission),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    val readExternalStorage =
                        grantResults[0] === PackageManager.PERMISSION_GRANTED
                    val writeExternalStorage =
                        grantResults[1] === PackageManager.PERMISSION_GRANTED
                    if (readExternalStorage && writeExternalStorage) {
                        // perform action when allow permission success
                        dispatchTakePictureIntent(currentRequestFor)
                    } else {
                        Toast.makeText(
                            this,
                            "Allow permission for storage access!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == REQUEST_FILE_CHOOSE) && resultCode == RESULT_OK) {

            val path = FilePathUtils.passUri(data, applicationContext)

            if (path != null) {
                val f = File(path)
                val newFile = createImageFile()
                val bitmap = BitmapFactory.decodeFile(f.path)
                val newBitMap =
                    setOrientationForImage(bitmap, f.path)
                newBitMap?.compress(Bitmap.CompressFormat.JPEG, 40, FileOutputStream(newFile))
                profile_edit_screen_profile_pic_iv.setImageBitmap(newBitMap)
                selectedFile = f

                println("selectedFile Gallery :: 1  $selectedFile")
                println("selectedFile Gallery :: 2 ${selectedFile.name}")

                updateFile()

            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                val imageBitmap = data!!.extras!!.get("data") as Bitmap
                val imageUri = getImageUri(applicationContext, imageBitmap)
                println("Real Path:: ${getRealPathFromURI(imageUri)}")

                val path = getRealPathFromURI(imageUri)
                val f = File(path)
                val newFile = createImageFile()
                val bitmap = BitmapFactory.decodeFile(f.path)
                val newBitMap =
                    setOrientationForImage(bitmap, f.path)
                newBitMap?.compress(Bitmap.CompressFormat.JPEG, 40, FileOutputStream(newFile))
                profile_edit_screen_profile_pic_iv.setImageBitmap(newBitMap)
                selectedFile = f

                println("selectedFile Camera :: 1  $selectedFile")
                println("selectedFile Camera :: 2 ${selectedFile.name}")

                updateFile()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION && resultCode == RESULT_OK) {

            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    dispatchTakePictureIntent(currentRequestFor)
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this@ProfileEditActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this@ProfileEditActivity, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun updateFile() {

        println("selectedFile.name == ${selectedFile.name}")

        val id: RequestBody =
            loginID.toRequestBody(contentType = "text/plain".toMediaTypeOrNull())
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

                val response = apiService.updateProfile(
                    resources.getString(R.string.api_access_token),
                    id, multiPartFile
                )


                if (response.isSuccessful) {
                    println("Check:::::::::::::::::::::: JSon Body if  ${response.body()}")
                    if (response.body()!!.has("url")) {
                        Toast.makeText(
                            applicationContext,
                            "Image uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        getUserDetails()
                    } else {
                        Toast.makeText(applicationContext, "Please try again", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    println("Check:::::::::::::::::::::: JSon Body else  ${response.body()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }
    private fun getUserDetails() {

        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(intercept())
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


                            println("GetUseDetails = ${CommonData.getUserDetails!!.profile_picture}")

                            sharedPreference.saveSession("player_photo_url", CommonData.getUserDetails!!.profile_picture)
                            sharedPreference.saveSession("player_experience_points", CommonData.getUserDetails!!.experience)
                            sharedPreference.saveSession("player_register_date", CommonData.getUserDetails!!.created)
                            sharedPreference.saveSession("player_user_name", CommonData.getUserDetails!!.username)
                            sharedPreference.saveSession("player_email_id", CommonData.getUserDetails!!.email)
                            sharedPreference.saveSession("player_rank", CommonData.getUserDetails!!.rank)
                            sharedPreference.saveSession("player_id", CommonData.getUserDetails!!.id)

                        } else {

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
        return interceptors
    }


}