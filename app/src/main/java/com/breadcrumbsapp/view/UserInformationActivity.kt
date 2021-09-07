package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.UserInformationLayoutBinding
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import kotlinx.android.synthetic.main.user_information_layout.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserInformationActivity : AppCompatActivity() {
    private val originalFormat: DateFormat =
        SimpleDateFormat("yyyy-mm-DD HH:MM:SS", Locale.ENGLISH)  //2020-11-27 12:27:04
    val targetFormat: DateFormat = SimpleDateFormat("DD MMM yyyy", Locale.ENGLISH)
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private lateinit var binding: UserInformationLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserInformationLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass = SessionHandlerClass(applicationContext)

        if (CommonData.getUserDetails != null) {

            println("Data from :: Model class")
            val createdDateStr: String = CommonData.getUserDetails!!.created
            try {


                val postCreatedDate: Date = originalFormat.parse(createdDateStr)
                val formattedDate: String = targetFormat.format(postCreatedDate)

                created_date_view.text = formattedDate
            } catch (e: Exception) {
                e.printStackTrace()
                created_date_view.text = createdDateStr
            }

            if(CommonData.getUserDetails!!.username!="")
            {

                nickname_text_view.text = CommonData.getUserDetails!!.username
            }
            else{
                nickname_text_view.text = sessionHandlerClass.getSession("player_name")
            }
            user_id_text_view.text = "#" + CommonData.getUserDetails!!.id
            email_address_text_view.text = CommonData.getUserDetails!!.email
        } else {
            println("Data from :: Session class")
            if(sessionHandlerClass.getSession("player_user_name")!="")
            {

                nickname_text_view.text = sessionHandlerClass.getSession("player_user_name")
            }
            else{
                //"player_name"
                nickname_text_view.text = sessionHandlerClass.getSession("player_name")
            }
            user_id_text_view.text = "#" +sessionHandlerClass.getSession("player_id")
            email_address_text_view.text = sessionHandlerClass.getSession("player_email_id")
            val createdDateStr: String? = sessionHandlerClass.getSession("player_register_date")

            try {


                val postCreatedDate: Date = originalFormat.parse(createdDateStr)
                val formattedDate: String = targetFormat.format(postCreatedDate)

                created_date_view.text = formattedDate
            } catch (e: Exception) {
                e.printStackTrace()
                created_date_view.text = createdDateStr
            }
        }
        println("User Info NickName :: ${nickname_text_view.text}")
        println("User Info UserID :: ${user_id_text_view.text}")
        user_information_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

}