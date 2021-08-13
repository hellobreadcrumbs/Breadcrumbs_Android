package com.breadcrumbsapp.view.rewards.rewards_details

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.RewardsScreenDetailsLayoutBinding
import com.breadcrumbsapp.view.TrailsDetailsActivity
import com.breadcrumbsapp.view.rewards.GetRewardsDataModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rewards_screen_details_layout.*
import java.lang.Exception
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
           // tv_reward_details_desc.text = "Redeem this reward by $enddate"
            val originalFormat: DateFormat = SimpleDateFormat("yyyy-mm-DD HH:MM:SS", Locale.ENGLISH)  //2020-11-27 12:27:04
            val targetFormat: DateFormat = SimpleDateFormat("DD MMM yyyy")

            try
            {
                println("formattedDate $scan_date")
                val postCreatedDate: Date = originalFormat.parse(scan_date)
                val formattedDate: String = targetFormat.format(postCreatedDate)
                println("formattedDate $formattedDate")
                tv_reward_details_redeem_date.text = "REDEEM BY $formattedDate"
            }
            catch (e:Exception)
            {
                e.printStackTrace()
                tv_reward_details_redeem_date.text = "REDEEM BY $scan_date"
            }


            tv_reward_details_show_title.text = redeem_msg_title
            tv_reward_details_show_message.text = redeem_msg_body
            var imagePath=resources.getString(R.string.staging_url)+reward_img
            Glide.with(applicationContext).load(imagePath)
                .into(iv_reward_details_image)

            //redeem_status == 1 means redeemed already, so disabling the whole view
            if (redeem_status == "0") {
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

    private fun setOnclickListeners() {
        tv_reward_details_redeem.setOnClickListener {
            val qrData = Bundle().apply {
                putString(MESSAGE_QR, message.qr)
                putString(MESSAGE_QR_TITLE, message.rewardtitle)
            }

            startActivity(
                Intent(
                    applicationContext,
                    RewardsRedeemDetailsActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtras(qrData)
            )
        }

        reward_details_screen_back_button.setOnClickListener {
            finish()
        }
        iv_reward_details_show_info.setOnClickListener(View.OnClickListener {

           /* startActivity(
                Intent(
                    applicationContext,
                    TrailsDetailsActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            )*/
            Toast.makeText(applicationContext, "Under Construction", Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        const val MESSAGE_QR = "redeem_qr_code"
        const val MESSAGE_QR_TITLE = "redeem_title"
        const val TEXT_REDEEMED = "REDEEMED"
    }
}