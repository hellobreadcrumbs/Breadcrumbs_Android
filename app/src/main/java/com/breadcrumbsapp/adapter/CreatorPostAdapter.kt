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
import android.widget.ToggleButton
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.readmoretextview.ReadMoreTextView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.model.GetFeedDataModel
import com.breadcrumbsapp.model.GetMyFeedModel
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.creator_post_layout.*
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


internal class CreatorPostAdapter(getFeed: List<GetFeedDataModel.Message>) :
    RecyclerView.Adapter<CreatorPostAdapter.MyViewHolder>() {

    private var getFeedsLocalObj: List<GetFeedDataModel.Message> = getFeed
    private lateinit var context: Context
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon

    )

    private var trailNameString: Array<String> = arrayOf("PIONEER TRAIL","WILD ABOUT TWILIGHT TRAIL","Hanse & Grey's Adventure")


    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imageView: ImageView = view.findViewById(R.id.creator_post_screen_imageView)
        var shareIcon: ImageView = view.findViewById(R.id.shareIcon)
        var likeCountText: TextView = view.findViewById(R.id.likeCount)
        var shareText: TextView = view.findViewById(R.id.shareText)
        // var descriptionContent: TextView = view.findViewById(R.id.descriptionContent)
        var descriptionContent: ReadMoreTextView = view.findViewById(R.id.descriptionContent)

        var userProfilePicture: CircularImageView =
            view.findViewById(R.id.creator_post_profile_picture)
        var createdDateTextView: TextView = view.findViewById(R.id.createdDateTextView)
        var likeButton: ToggleButton = view.findViewById(R.id.creator_post_adapter_likeImageView)
        var  username:TextView=view.findViewById(R.id.creator_post_screen_username)

    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.creator_post_adapter, parent, false)
        context = parent.context
        sessionHandlerClass= SessionHandlerClass(context)
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

        println("getFeedsLocalObj:: ${data.username}")

       // holder.username.text=data.username

        val localImageObj =
            context.resources.getString(R.string.staging_url) + data.photo_url

        val localProfilePic =
            context.resources.getString(R.string.staging_url) + data.profile_picture

        println("localProfilePic = $localImageObj")

        Glide.with(context)
            .load(localImageObj)
            .into(holder.imageView)

        println("Like Count = $position , ${data.like_count}")
        if (data.like_count <= "1") {
            holder.likeCountText.text = data.like_count + " Like"
        } else {
            holder.likeCountText.text = data.like_count + " Likes"
        }

        println("Text length : ${data.description.length}")



        holder.descriptionContent.text=data.description




           /* Glide.with(context)
                .load(R.drawable.breadcrumbs_trail).placeholder(R.drawable.breadcrumbs_trail)
                .into(holder.userProfilePicture)
*/

        if(sessionHandlerClass.getSession("temp_trail_id")=="4")
        {
            Glide.with(context).load(trailIcons[1]).into(holder.userProfilePicture)
            holder.username.text=trailNameString[1]

        }
        else if(sessionHandlerClass.getSession("temp_trail_id")=="6")
        {
            Glide.with(context).load(trailIcons[2]).into(holder.userProfilePicture)
            holder.username.text=trailNameString[2]

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

            }

            holder.shareText.setOnClickListener {
                val drawable  = holder.imageView.drawable as BitmapDrawable
                val bitmap=drawable.bitmap as Bitmap
                saveBitmapAsImageToDevice(bitmap)

            }



        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }



    override fun getItemCount(): Int {
        return getFeedsLocalObj.size
    }
}