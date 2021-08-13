package com.breadcrumbsapp.view.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.ProfileEditLayoutBinding
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.profile_edit_layout.*
import kotlinx.android.synthetic.main.profile_edit_layout.profile_edit_screen_profile_pic_iv
import kotlinx.android.synthetic.main.user_profile_screen_layout.*
import java.io.ByteArrayOutputStream


class ProfileEditActivity : AppCompatActivity() {
    private lateinit var sharedPreference: SessionHandlerClass
    private lateinit var binding: ProfileEditLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileEditLayoutBinding.inflate(layoutInflater)
        sharedPreference = SessionHandlerClass(applicationContext)
        setContentView(binding.root)
        nick_name_text.text = sharedPreference.getSession("player_name")

        profile_edit_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()

        })

        profile_edit_screen_profile_pic_iv.setOnClickListener(View.OnClickListener {

            choosePhoto()
        })

        if(sharedPreference.getSession("player_photo_url")!=null && sharedPreference.getSession("player_photo_url")!="")
        {

            Glide.with(applicationContext).load(sharedPreference.getSession("player_photo_url")).into(profile_edit_screen_profile_pic_iv)
        }
        else
        {
            Glide.with(applicationContext).load(R.drawable.no_image).into(profile_edit_screen_profile_pic_iv)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)




        if (resultCode !== RESULT_CANCELED) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val resultUri = result.uri
                    println("Cropped Image : $resultUri")
                    profile_edit_screen_profile_pic_iv.setImageURI(resultUri)


                    //    constraintLayout.setBackgroundColor(Color.parseColor("#F8F0DD"))
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            } else {
                when (requestCode) {
                    0 -> {

                        var bmp: Bitmap = data!!.extras!!.get("data") as Bitmap
                        profile_edit_screen_profile_pic_iv.setImageBitmap(bmp)
                    }
                    1 -> {
                        try {


                            var imageUri: Uri? = data?.data
                            profile_edit_screen_profile_pic_iv.setImageURI(imageUri)

                            /* println("bmp::: ${data!!.extras!!.get("data") }")
                             var bmp: Bitmap = data!!.extras!!.get("data") as Bitmap


                             var selectedImage:Uri=getImageUri(applicationContext,bmp)

                             val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                             if (selectedImage != null) {
                                 val cursor: Cursor? = contentResolver.query(
                                     selectedImage,
                                     filePathColumn, null, null, null
                                 )
                                 if (cursor != null) {
                                     cursor.moveToFirst()
                                     val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                                     val picturePath: String = cursor.getString(columnIndex)
                                     profile_edit_screen_profile_pic_iv.setImageBitmap(
                                         BitmapFactory.decodeFile(
                                             picturePath
                                         )
                                     )
                                     cursor.close()


                                 }
                             }*/
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
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


        var cameraButton = dialog.findViewById(R.id.tv_camera) as TextView
        var galleryButton = dialog.findViewById(R.id.tv_gallery) as TextView
        var popCloseButton = dialog.findViewById(R.id.pop_up_close_btn) as ImageView

        cameraButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePicture, 0)
        })

        galleryButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            /*     val pickPhoto =
                     Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                 println("pickPhoto $pickPhoto")
                 startActivityForResult(pickPhoto, 1)


                 */

            Toast.makeText(applicationContext, "Under Construction", Toast.LENGTH_SHORT).show()


        })

        popCloseButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })




        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

/*    private fun selectImage(context: Context) {


        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setTitle("Choose your profile picture")
        dialog.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 0)
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 1)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        dialog.show()
    }*/


}