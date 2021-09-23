package com.breadcrumbsapp.view.rewards.rewards_details

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.text.TextUtils.SimpleStringSplitter
import android.text.TextUtils.StringSplitter
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.RewardsScreenDetailsLayoutBinding
import com.breadcrumbsapp.view.rewards.GetRewardsDataModel
import com.breadcrumbsapp.view.rewards.RewardsScreenActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.discover_details_screen.*
import kotlinx.android.synthetic.main.rewards_screen_details_layout.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class RewardsDetailsActivity : AppCompatActivity() {
    private lateinit var message: GetRewardsDataModel.Message
    private lateinit var rewardsScreenDetailsLayoutBinding: RewardsScreenDetailsLayoutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rewardsScreenDetailsLayoutBinding =
            RewardsScreenDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(rewardsScreenDetailsLayoutBinding.root)

        message = intent.extras?.get("rewardModelData") as GetRewardsDataModel.Message
        displayUiData(message)
        setOnclickListeners()


    }

    private fun displayUiData(getRewardsDataModelMessage: GetRewardsDataModel.Message) {
        with(getRewardsDataModelMessage) {
            tv_reward_details_title.text = rewardtitle
            tv_reward_details_show_message.text=redeem_msg_body
            redeem_details_tv.text=details+"\n\n"+redeem_msg_body

            //tv_reward_details_terms_conditions_one.text=Html.fromHtml(tnc)

            try
            {
                println("tnc == $tnc")
                if(tnc!="null")
                {
                    tv_reward_details_terms_conditions_one.visibility=View.VISIBLE
                    tv_reward_details_terms_conditions.visibility=View.VISIBLE
                    tv_reward_details_terms_conditions_one.text=tnc.replace("\\n", "\n")
                }
                else
                {
                    tv_reward_details_terms_conditions_one.visibility=View.GONE
                    tv_reward_details_terms_conditions.visibility=View.GONE
                }

            }
            catch (e:NullPointerException)
            {
                e.printStackTrace()
                tv_reward_details_terms_conditions_one.visibility=View.GONE
                tv_reward_details_terms_conditions.visibility=View.GONE
            }
            val originalFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)  //2020-11-27 12:27:04
            val targetFormat: DateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)


            try
            {
                println("formattedDate $enddate")
                val postCreatedDate: Date = originalFormat.parse(enddate)
                val formattedDate: String = targetFormat.format(postCreatedDate)
                println("formattedDate $formattedDate")
                tv_reward_details_redeem_date.text = "Valid until $formattedDate"
            }
            catch (e:Exception)
            {
                e.printStackTrace()
                //Valid until 30 JUNE 2021
                tv_reward_details_redeem_date.text = "Valid until $enddate"
            }


            tv_reward_details_show_title.text = redeem_msg_title
            tv_reward_details_show_message.text = redeem_msg_body
            var imagePath=resources.getString(R.string.staging_url)+redeem_msg_img

           /* Glide.with(applicationContext).load(imagePath)
                .into(iv_reward_details_image)*/

            try {


                Glide.with(applicationContext)
                    .load(imagePath)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv_reward_details_loader.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv_reward_details_loader.visibility = View.GONE
                            return false
                        }
                    })
                    .into(iv_reward_details_image)

            } catch (e: Exception) {
                e.printStackTrace()
            }




            //redeem_status == 1 means redeemed already, so disabling the whole view
            if (redeem_status == "1") {
                tv_reward_details_redeem.text = TEXT_REDEEMED
                tv_reward_details_redeem.background =
                    ContextCompat.getDrawable(applicationContext, R.drawable.round_grey_button)
                ll_reward_details_title_desc.background =
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.round_shadow_button_blue_disabled
                    )
                tv_reward_details_redeem.isEnabled = false
                iv_reward_details_show_info.isEnabled = false
            }
        }
    }

    private fun showAlert()
    {
        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.redeem_success_layout)
        dialog.window?.setDimAmount(0.3f)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val imageV=dialog.findViewById(R.id.about_screen_trail_icon) as ImageView
        val okButton = dialog.findViewById(R.id.successButton) as TextView
        val redeemContent = dialog.findViewById(R.id.redeem_content) as TextView
        val redeemTitle = dialog.findViewById(R.id.success_redeem_title) as TextView

        imageV.visibility=View.GONE

        redeemTitle.text="Alert!"
        redeemContent.text = "Only open this page when you're ready to scan"
        okButton.setOnClickListener(View.OnClickListener {

            dialog.dismiss()
            val qrData = Bundle().apply {
                putString(MESSAGE_QR, message.qr)
                putString(MESSAGE_QR_TITLE, message.redeem_msg_title)
                putString(REDEEM_DETAILS, message.details)
                putString(REDEEM_MSG_BODY, message.redeem_msg_body)
                putString(REWARD_ID, message.id)
            }

            startActivity(
                Intent(
                    applicationContext,
                    RewardsRedeemDetailsActivity::class.java
                )  .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtras(qrData)
            )
        })


        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }
    private fun setOnclickListeners() {
        tv_reward_details_redeem.setOnClickListener {

            showAlert()
        }

        reward_details_screen_back_button.setOnClickListener {
            startActivity(Intent(applicationContext, RewardsScreenActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
        iv_reward_details_show_info.setOnClickListener(View.OnClickListener {

           /* startActivity(
                Intent(
                    applicationContext,
                    TrailsDetailsActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) .putExtra("position", 0)
                        .putExtra("getTrailsListData", CommonData.getTrailsData!![0])

            )*/
          //  Toast.makeText(applicationContext, "Under Construction", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        startActivity(Intent(applicationContext, RewardsScreenActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
    companion object {
        const val MESSAGE_QR = "redeem_qr_code"
        const val MESSAGE_QR_TITLE = "redeem_title"
        const val TEXT_REDEEMED = "REDEEMED"
        const val REDEEM_DETAILS = "details"
        const val REDEEM_MSG_BODY = "redeem_msg_body"
        const val REWARD_ID = "id"
    }
}