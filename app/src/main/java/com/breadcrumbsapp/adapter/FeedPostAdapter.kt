package com.breadcrumbsapp.adapter

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.NonNull
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.model.GetFeedDataModel
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


// For toggle Animation
//https://medium.com/@rashi.karanpuria/create-beautiful-toggle-buttons-in-android-64d299050dfb

internal class FeedPostAdapter(getFeed: List<GetFeedDataModel.Message>, loginID: String?) :
    ActivityCompat.OnRequestPermissionsResultCallback,
    RecyclerView.Adapter<FeedPostAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<GetFeedDataModel.Message> = getFeed
    private lateinit var context: Context
    private var interceptor = intercept()
    private var local_loginID = loginID

    private lateinit var sessionHandlerClass: SessionHandlerClass
    private val REQUEST_STORAGE_PERMISSION = 505
    private lateinit var bitmap: Bitmap

    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageParentLayout: LinearLayoutCompat = view.findViewById(R.id.adapter_imageParentLayout)
        var imageView: ImageView = view.findViewById(R.id.postImage)
        var shareIcon: ImageView = view.findViewById(R.id.shareIcon)
        var likeCountText: TextView = view.findViewById(R.id.likeCount)
        var shareText: TextView = view.findViewById(R.id.shareText)

        var descriptionContent: ReadMoreTextView = view.findViewById(R.id.descriptionContent)
        var userNameTextView: TextView = view.findViewById(R.id.userName)
        var trailName: TextView = view.findViewById(R.id.feed_post_banner_trail_name)
        var userProfilePicture: CircularImageView = view.findViewById(R.id.feedPostUserProfilePicture)
        var trailPic: CircularImageView = view.findViewById(R.id.feed_post_banner_trail_image)
        var createdDateTextView: TextView = view.findViewById(R.id.createdDateTextView)

        var likeButton: ToggleButton = view.findViewById(R.id.feed_layout_adapter_likeImageView)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_layout_adapter, parent, false)
        context = parent.context
        sessionHandlerClass = SessionHandlerClass(context)

        return MyViewHolder(itemView)
    }

    private fun saveBitmapAsImageToDevice(bitmap: Bitmap?) {
        // Add a specific media item.
        val resolver = context.contentResolver

        val imageStorageAddress = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "my_app_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
        }




        try {
            // Save the image.
            val contentUri: Uri? = resolver.insert(imageStorageAddress, imageDetails)
            contentUri?.let { uri ->
                // Don't leave an orphan entry in the MediaStore
                if (bitmap == null) resolver.delete(contentUri, null, null)
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                outputStream?.let { outStream ->
                    val isBitmapCompressed =
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 95, outStream)
                    if (isBitmapCompressed == true) {
                        outStream.flush()
                        outStream.close()
                    }
                    println("contentUri $contentUri")
                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        type = "image/jpeg"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Send To"))
                } ?: throw IOException("Failed to get output stream.")
            } ?: throw IOException("Failed to create new MediaStore record.")
        } catch (e: IOException) {
            throw e
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val data = getFeedsLocalObj[position]

        println("Feed position : ${getFeedsLocalObj.size}")
        println("Feed name : IF =  ${data.name}")

        holder.likeButton.isClickable = local_loginID != data.user_id
        if(local_loginID == data.user_id)
        {
            if(data.like_count.toInt()>0)
            {
                holder.likeButton.isChecked = true
            }
        }
        // holder.likeButton.isChecked = true

        val localImageObj =
            context.resources.getString(R.string.staging_url) + data.photo_url

        val localProfilePic =
            context.resources.getString(R.string.staging_url) + data.profile_picture

        println("localProfilePic = $localImageObj")


        Glide.with(context)
            .load(localImageObj)
            .into(holder.imageView)

        println("Like Count = ${getFeedsLocalObj[position]} , ${data.like_count}")
        if (data.like_count <= "1") {
            holder.likeCountText.text = data.like_count + " Like"
        } else {
            holder.likeCountText.text = data.like_count + " Likes"
        }


        // For Like Button Animation effect..
        val scaleAnimation = ScaleAnimation(
            0.7f,
            1.0f,
            0.7f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.7f,
            Animation.RELATIVE_TO_SELF,
            0.7f
        )
        scaleAnimation.duration = 500
        val bounceInterpolator = BounceInterpolator()
        scaleAnimation.interpolator = bounceInterpolator

        holder.likeButton.setOnCheckedChangeListener { b, isChecked ->

            b.startAnimation(scaleAnimation)
            holder.likeCountText.startAnimation(scaleAnimation)
            println("Feed Like = $isChecked")
           /* if (isChecked) {
                getFeedPostLikeDetails(data.f_id, holder, position)
            } else {
                getFeedPostLikeDetails(data.f_id, holder, position)
            }*/

            getFeedPostLikeDetails(data.f_id, holder, position)
        }

        holder.descriptionContent.text = data.description

        holder.userNameTextView.text = data.username

        holder.trailName.text = data.title

        val trailImagePath =
            context.resources.getString(R.string.staging_url) + data.map_icon_dt_url

        Glide.with(context)
            .load(trailImagePath).placeholder(R.drawable.no_image)
            .into(holder.trailPic)

        if (data.profile_picture == "") {
            Glide.with(context)
                .load(R.drawable.no_image)
                .into(holder.userProfilePicture)
        } else {
            Glide.with(context)
                .load(localProfilePic)
                .into(holder.userProfilePicture)
        }


        //"created": "2021-07-26 06:45:47",

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val serverFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")



        try {
            val postCreatedDate: Date = dateFormat.parse(data.created)
            val currentDate = Date()
            println("Date Is :  Old Date = $postCreatedDate , Today Date = $currentDate")


            val postCreatedDate1: Date = serverFormat.parse(data.created)
            println("Date Is :  New Format= $postCreatedDate1")


            //  val diff = postCreatedDate.time - currentDate.time
            val diff = currentDate.time - postCreatedDate.time
            val seconds: Int = (diff / 1000).toInt()
            val minutes: Int = (seconds / 60).toInt()
            val hours: Int = (minutes / 60).toInt()
            val days: Int = (hours / 24).toInt()
            println("Date Is :  Remaining Date: $days")



            if (days > 0) {
                if (days == 1) {
                    holder.createdDateTextView.text = "$days Day Ago"
                } else {
                    holder.createdDateTextView.text = "$days Days Ago"
                }
            } else if (hours in 1..23) {
                if (hours == 1) {
                    holder.createdDateTextView.text = "$hours Hour Ago"
                } else {
                    holder.createdDateTextView.text = "$hours Hours Ago"
                }

            } else if (minutes in 1..59) {
                if (minutes == 1) {
                    holder.createdDateTextView.text = "$minutes Minute Ago"
                } else {
                    holder.createdDateTextView.text = "$minutes Minutes Ago"
                }
            }
            else if (seconds in 1..59) {
                if (seconds == 1) {
                    holder.createdDateTextView.text = "$seconds Second Ago"
                } else {
                    holder.createdDateTextView.text = "$seconds Seconds Ago"
                }
            }

            holder.shareIcon.setOnClickListener {

//                bitmap = getBitmapFromView(holder.imageParentLayout)!!
//                saveBitmapAsImageToDevice(bitmap)
                if (checkStoragePermission(context)) {
                    println("requestCode icon if.. ")
                    bitmap = getBitmapFromView(holder.imageParentLayout)!!
                    saveBitmapAsImageToDevice(bitmap)
                } else {
                    println("requestCode icon else.. ")
                    requestPermissions(
                        context as Activity,
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        REQUEST_STORAGE_PERMISSION
                    )
                }
            }

            holder.shareText.setOnClickListener {

                if (checkStoragePermission(context)) {
                    println("requestCode text if.. ")
                    bitmap = getBitmapFromView(holder.imageParentLayout)!!
                    saveBitmapAsImageToDevice(bitmap)
                } else {
                    println("requestCode text else.. ")
                    requestPermissions(
                        context as Activity,
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        REQUEST_STORAGE_PERMISSION
                    )
                }

            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    private fun checkStoragePermission(mContext: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            val result1 =
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    override fun getItemCount(): Int {
        return getFeedsLocalObj.size
    }

    private fun getFeedPostLikeDetails(feedID: String, holder: MyViewHolder, position: Int) {
        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(context.resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()


            jsonObject.put("user_id", local_loginID)
            jsonObject.put("feed_id", feedID)


            println("like API Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getFeedPostLikeDetails(
                    context.resources.getString(R.string.api_access_token),
                    requestBody
                )

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

                    val status: Boolean = jsonObject!!.get("status")!!.asBoolean
                    val message: String = jsonObject.get("message")!!.asString
                    println("like API Status = $status")
                    println("like API message = $message")

                    if (status) {
                        getFeedPostData(feedID, holder, position)
                    }

                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getFeedPostData(feedID: String, holder: MyViewHolder, position: Int) {
        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(context.resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()

            jsonObject.put("user_id", local_loginID)
            println("getFeedPostData Input = $jsonObject")

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)

            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getFeedDetails(
                    context.resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    if (response.body()!!.status) {

                        CommonData.getFeedData = response.body()?.message

                        if (CommonData.getFeedData != null) {
                            for (i in CommonData.getFeedData!!.indices) {
                                if (feedID==CommonData.getFeedData!![i].f_id) {


                                    if(CommonData.getFeedData!![i].ul_id!=null)
                                    {
                                        CommonData.getFeedData!![i].ul_id= null.toString()
                                        val likeCount=CommonData.getFeedData!![i].like_count.toInt()
                                        holder.likeCountText.text = "${likeCount-1} Like"
                                    }
                                    else
                                    {
                                        CommonData.getFeedData!![i].ul_id="0"
                                        val likeCount=CommonData.getFeedData!![i].like_count.toInt()
                                        holder.likeCountText.text = "${likeCount+1} Like"
                                    }

                                    println("like API like_count = ${CommonData.getFeedData!![i].like_count}")

                                    try {


                                        context.runOnUiThread {
                                            if (CommonData.getFeedData!![i].like_count == "1") {
                                                holder.likeCountText.text =
                                                    CommonData.getFeedData!![i].like_count + " Like"

                                            } else if (CommonData.getFeedData!![i].like_count == "0") {
                                                holder.likeCountText.text =
                                                    CommonData.getFeedData!![i].like_count + " Like"

                                            } else {
                                                holder.likeCountText.text =
                                                    CommonData.getFeedData!![i].like_count + " Likes"
                                            }

                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        println("requestCode onRequestPermissionsResult = $requestCode")
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    println("requestCode IF = $requestCode")
                    saveBitmapAsImageToDevice(bitmap)
                } else {
                    println("requestCode ELSE = $requestCode")
                }
            }
        }
    }
}