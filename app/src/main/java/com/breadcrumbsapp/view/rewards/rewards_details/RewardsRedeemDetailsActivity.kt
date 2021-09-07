package com.breadcrumbsapp.view.rewards.rewards_details


import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.RewardsScreenQrScanLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.rewards.GetRewardsDataModel
import com.breadcrumbsapp.view.rewards.RewardsScreenActivity
import com.breadcrumbsapp.view.rewards.rewards_details.RewardsDetailsActivity.Companion.MESSAGE_QR
import com.breadcrumbsapp.view.rewards.rewards_details.RewardsDetailsActivity.Companion.MESSAGE_QR_TITLE
import com.breadcrumbsapp.view.rewards.rewards_details.RewardsDetailsActivity.Companion.REDEEM_DETAILS
import com.breadcrumbsapp.view.rewards.rewards_details.RewardsDetailsActivity.Companion.REWARD_ID
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.rewards_screen_qr_scan_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class RewardsRedeemDetailsActivity : AppCompatActivity() {
    private lateinit var rewardsScreenQrScanLayoutBinding: RewardsScreenQrScanLayoutBinding
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private lateinit var popStr: String
    private var message: String? = ""
    private var redeemMsgBody: String? = ""
    private var selectedRewardID: String? = ""
    private var interceptor = intercept()
    private lateinit var rewardsListModel: List<GetRewardsDataModel.Message>

    private lateinit var myTimer: Timer
    private lateinit var handler:HandlerThread
    val mainHandler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rewardsScreenQrScanLayoutBinding =
            RewardsScreenQrScanLayoutBinding.inflate(layoutInflater)
        setContentView(rewardsScreenQrScanLayoutBinding.root)
        sessionHandlerClass = SessionHandlerClass(applicationContext)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val redeemQrCode = bundle.getString(MESSAGE_QR)
            message = bundle.getString(MESSAGE_QR_TITLE)
            val redeemDetails = bundle.getString(REDEEM_DETAILS)
            selectedRewardID = bundle.getString(REWARD_ID)




            redeemMsgBody = bundle.getString(RewardsDetailsActivity.REDEEM_MSG_BODY)
            popStr = message + "\n" + redeemMsgBody
            displayUiData(redeemQrCode, message, redeemDetails, redeemMsgBody)
        }

        reward_qr_details_screen_back_button.setOnClickListener {
            finish()
        }



        mainHandler.post(updateTextTask)

    }
    private val updateTextTask = object : Runnable {
        override fun run() {
            getRewardsList()
            mainHandler.postDelayed(this, 1000)
        }
    }

    private fun displayUiData(
        qrCode: String?,
        messageTitle: String?,
        redeemDetails: String?,
        redeemMsgBody: String?
    ) {
        println(qrCode + messageTitle)
        if (qrCode != null) {
            txt_qr_details_message_code.text = qrCode
            val generateQR = "$qrCode-${sessionHandlerClass.getSession("login_id")}"
            createQRCode(generateQR)
        }

        txt_qr_details_message_code_title.text = messageTitle
        txt_qr_details_message.text = redeemDetails


    }


    private fun createQRCode(redeemQrCode: String) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(redeemQrCode, BarcodeFormat.QR_CODE, 600, 600)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.parseColor("#EBE5D6")
                    )
                }
            }
            iv_reward_details_image.setImageBitmap(bmp)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun redeemSuccessPopup(popStr: String) {


        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.redeem_success_layout)
        dialog.window?.setDimAmount(0.3f)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val okButton = dialog.findViewById(R.id.successButton) as TextView
        val redeemContent = dialog.findViewById(R.id.redeem_content) as TextView

        redeemContent.text = popStr
        okButton.setOnClickListener(View.OnClickListener {

            dialog.dismiss()
            startActivity(Intent(applicationContext, RewardsScreenActivity::class.java))
        })


        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }

    override fun onResume() {
        super.onResume()
        showAlert()
    }
    private fun showAlert()
    {
        val dialog = Dialog(applicationContext, R.style.FirebaseUI_Transparent)

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

        imageV.visibility=View.GONE

        redeemContent.text = "Only open this page when you're ready to scan"
        okButton.setOnClickListener(View.OnClickListener {

            dialog.dismiss()

        })


        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }
    private fun getRewardsList() {
        try {

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
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", sessionHandlerClass.getSession("login_id"))

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)


            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getRewardList(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                if (response.isSuccessful) {
                    try {
                        if (response.body()!!.status) {

                            rewardsListModel = response.body()!!.message

                            println("myTimer : working.. ")
                            runOnUiThread {

                                for (i in rewardsListModel.indices) {
                                    if (rewardsListModel[i].id == selectedRewardID) {
                                        val redeemStatus = rewardsListModel[i].redeem_status
                                        if (redeemStatus == "1") {

                                            mainHandler.removeCallbacks(updateTextTask)
                                            println("myTimer : cancel.. ")
                                            redeemSuccessPopup(popStr)
                                        }
                                    }

                                }
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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


}