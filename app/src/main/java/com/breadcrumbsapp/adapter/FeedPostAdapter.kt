package com.breadcrumbsapp.adapter

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetFeedDataModel
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


internal class FeedPostAdapter(getFeed: List<GetFeedDataModel.Message>) :
    RecyclerView.Adapter<FeedPostAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<GetFeedDataModel.Message> = getFeed
    private lateinit var context: Context


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.postImage)
        var shareIcon: ImageView = view.findViewById(R.id.shareIcon)
        var likeCountText: TextView = view.findViewById(R.id.likeCount)
        var shareText: TextView = view.findViewById(R.id.shareText)
       // var descriptionContent: TextView = view.findViewById(R.id.descriptionContent)
        var descriptionContent: ReadMoreTextView = view.findViewById(R.id.descriptionContent)
        var userNameTextView: TextView = view.findViewById(R.id.userName)
        var userProfilePicture: CircularImageView =
            view.findViewById(R.id.feedPostUserProfilePicture)
        var createdDateTextView: TextView = view.findViewById(R.id.createdDateTextView)


    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_layout_adapter, parent, false)
        context = parent.context
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

        var localImageObj =
            context.resources.getString(R.string.live_url) + data.photo_url

        var localProfilePic =
            context.resources.getString(R.string.live_url) + data.profile_picture

        println("localProfilePic = $localProfilePic")

        Glide.with(context)
            .load(localImageObj)
            .into(holder.imageView)

        println("Like Count = ${getFeedsLocalObj[position]} , ${data.like_count}")
        if (data.like_count <= "1") {
            holder.likeCountText.text = data.like_count + " Like"
        } else {
            holder.likeCountText.text = data.like_count + " Likes"
        }

        println("Text length : ${data.description.length}")



        holder.descriptionContent.text=data.description

        holder.userNameTextView.text = data.username

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

        try {
            val postCreatedDate: Date = dateFormat.parse(data.created)
            val currentDate = Date()
            println("Date Is :  Old Date = $postCreatedDate , Today Date = $currentDate")


            //  val diff = postCreatedDate.time - currentDate.time
            val diff = currentDate.time - postCreatedDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            println("Date Is :  Remaining Date: $days")


            if (minutes.equals(0)) {
                if (postCreatedDate.before(currentDate)) {

                    println("Date Is :  IF: $seconds")

                    holder.createdDateTextView.text = "$seconds Seconds Ago"
                } else {
                    println("Date Is :  ELSE: $seconds")
                }
            } else if (hours.equals(0)) {
                if (postCreatedDate.before(currentDate)) {

                    println("Date Is :  IF: $minutes")

                    holder.createdDateTextView.text = "$minutes Minutes Ago"
                } else {
                    println("Date Is :  ELSE: $minutes")
                }
            } else if (days.equals(0)) {
                if (postCreatedDate.before(currentDate)) {

                    println("Date Is :  IF: $hours")

                    holder.createdDateTextView.text = "$hours Hours Ago"
                } else {
                    println("Date Is :  ELSE: $hours")
                }
            } else {
                if (postCreatedDate.before(currentDate)) {

                    println("Date Is :  IF: $days")

                    holder.createdDateTextView.text = "$days Days Ago"
                } else {
                    println("Date Is :  ELSE: $days")
                }
            }

            holder.shareIcon.setOnClickListener {
                val drawable  = holder.imageView.drawable as BitmapDrawable
                 val bitmap=drawable.bitmap as Bitmap
                saveBitmapAsImageToDevice(bitmap)
                //saveImageToInternalStorage(holder.imageView.drawable)
               // shareMethod(localImageObj)
            }


            /*    val shareIntent: Intent = Intent().apply {
                               action = Intent.ACTION_SEND
                               putExtra(Intent.EXTRA_STREAM, uriToImage)
                               type = "image/jpeg"
                           }
                           context.startActivity(Intent.createChooser(shareIntent, "Send To"))*/
            // Log.e("toyBornTime", "" + toyBornTime);
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun shareMethod(uriString:String)
    {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val screenshotUri: Uri = Uri.parse(uriString)
        try {
            val stream: InputStream = context.contentResolver.openInputStream(screenshotUri)!!
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        sharingIntent.type = "image/jpeg"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
        context.startActivity(Intent.createChooser(sharingIntent, "Share image using"))
    }

    override fun getItemCount(): Int {
        return getFeedsLocalObj.size
    }
}