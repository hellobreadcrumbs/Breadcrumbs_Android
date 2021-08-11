package com.breadcrumbsapp.view.rewards.rewards_details

import android.R
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.RewardsScreenQrScanLayoutBinding
import com.breadcrumbsapp.view.rewards.rewards_details.RewardsDetailsActivity.Companion.MESSAGE_QR
import com.breadcrumbsapp.view.rewards.rewards_details.RewardsDetailsActivity.Companion.MESSAGE_QR_TITLE
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.rewards_screen_qr_scan_layout.*


class RewardsRedeemDetailsActivity : AppCompatActivity() {
    private lateinit var rewardsScreenQrScanLayoutBinding: RewardsScreenQrScanLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rewardsScreenQrScanLayoutBinding =
            RewardsScreenQrScanLayoutBinding.inflate(layoutInflater)
        setContentView(rewardsScreenQrScanLayoutBinding.root)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val redeemQrCode = bundle.getString(MESSAGE_QR)
            val message = bundle.getString(MESSAGE_QR_TITLE)
            displayUiData(redeemQrCode, message)
        }

        reward_qr_details_screen_back_button.setOnClickListener {
            finish()
        }
    }

    private fun displayUiData(qrCode: String?, messageTitle: String?) {
        println(qrCode + messageTitle)
        if (qrCode != null) {
            txt_qr_details_message_code.text=qrCode
            createQRCode(qrCode)
        }

        txt_qr_details_message_code_title.text = messageTitle
    }


    private fun createQRCode(redeemQrCode:String)
    {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(redeemQrCode, BarcodeFormat.QR_CODE, 600, 600)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.parseColor("#EBE5D6"))
                }
            }
            iv_reward_details_image.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}