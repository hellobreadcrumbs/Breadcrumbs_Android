package com.breadcrumbsapp.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.breadcrumbsapp.ARCoreActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.DiscoverDetailsScreenBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.model.GetEventsModel
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.discover_details_screen.*
import kotlinx.android.synthetic.main.quiz_challenge.*
import kotlinx.android.synthetic.main.quiz_challenge_question_activity.*
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


class DiscoverDetailsScreenActivity : YouTubeBaseActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
    }


    private lateinit var sharedPreference: SessionHandlerClass


    // private val youtubeApiKey =  "AIzaSyCxE4GNw8TJQOoYi6gebWcJ3KtUCTlCduE"
    private val youtubeApiKey = "AIzaSyDrMQduXjWkxg3nfqxXGiUCvpJTV84DCto"
    lateinit var binding: DiscoverDetailsScreenBinding

    var getEventsModel_Message: List<GetEventsModel.Message>? = null
    var getEventsModel: List<GetEventsModel>? = null
    private var poiID = ""
    private var poiName = ""
    private var poiDistance = ""
    private var poiImage = ""
    private var poiETA = ""
    private var poiQuestion = ""
    private var poiHint = ""
    private var poiChType = ""
    private var poiArid = ""
    private var poiQrCode = ""

    var challengeName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DiscoverDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SessionHandlerClass(applicationContext)


        var bundle: Bundle? = intent.extras
        var from = bundle!!.getString("from")

        poiName =sharedPreference.getSession("selectedPOIName").toString()
        poiID = sharedPreference.getSession("selectedPOIID").toString()
        poiDistance =sharedPreference.getSession("poiDistance").toString()
        poiImage = sharedPreference.getSession("selectedPOIImage").toString()
        poiETA = sharedPreference.getSession("selectedPOIDuration").toString()
        poiQuestion = sharedPreference.getSession("selectedPOIQuestion").toString()
        poiHint = sharedPreference.getSession("selectedPOIHintContent").toString()
        poiChType = sharedPreference.getSession("selectedPOIChallengeType").toString()
        poiArid =sharedPreference.getSession("selectedPOIARid").toString()
        poiQrCode = sharedPreference.getSession("selectedPOIQrCode").toString()

        println("poiQrCode $poiQrCode")


        if (poiArid == "1") {
            // needs open ARscreen
        }

        if (poiChType == "0") {
            challengeName = "selfie"
            challengeNameTv.text = "SELFIE CHALLENGE"
        } else {
            challengeName = "quiz"
            challengeNameTv.text = "QUIZ CHALLENGE"
        }


        if (poiHint == "" || poiHint == "null")
            if (poiHint == "" || poiHint == "null") {
                aboutContent.visibility = View.GONE
                aboutTitle.visibility = View.GONE
            } else {
                aboutContent.visibility = View.VISIBLE
                aboutTitle.visibility = View.VISIBLE
            }
        aboutContent.text = poiHint
        aboutContentImageView.visibility = View.GONE

        try {
            Glide.with(applicationContext).load(poiImage).into(binding.poiImageView)
            sharedPreference.saveSession("poi_image", poiImage)

        } catch (e: Exception) {
            e.printStackTrace()
        }


        travelETA.text = poiETA

        if (poiName != "") {
            binding.poiName.text = poiName
        }
        if (poiDistance != "") {
            binding.poiDistance.text = poiDistance
        }



        println("from::: $from")
        when {
            from.equals(resources.getString(R.string.take_me_there)) -> {

                binding.scannerIcon.alpha = 0.5f

            }
            from.equals(resources.getString(R.string.discover)) -> {
                binding.scannerIcon.alpha = 1f
                binding.takeMeThereBtn.text="ARRIVED"
                binding.takeMeThereBtn.background=getDrawable(R.drawable.arrived_btn)
                binding.discoverStatusText.text="UNDISCOVERED"
            }
            from== "discovered" -> {
                binding.scannerIcon.alpha = 0.5f
                binding.takeMeThereBtn.text="TAKE ME THERE"
                binding.takeMeThereBtn.background=getDrawable(R.drawable.take_me_there_bg)
                binding.discoverStatusText.text="DISCOVERED"

                detailsPoiBackGround.background=getDrawable(R.drawable.trail_banner_discovered)
            }
        }

        binding.cameraIconLayout.setOnClickListener {

            if (from.equals("take_me_there")) {

                // do nothing

            } else if (from.equals(resources.getString(R.string.discover))) {

                if (checkPermission()) {

                     openScannerScreen()



                } else {
                    requestPermission()
                }

            }

        }


        binding.backButton.setOnClickListener {
            finish()
        }

        // Get reference to the view of Video player
        binding.ytPlayer.initialize(youtubeApiKey, object : YouTubePlayer.OnInitializedListener {

            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                p2: Boolean
            ) {


                // It will helps to load the video and play by auto.
                //  player?.loadVideo("CzUslqxBwz0")  //https://youtu.be/CzUslqxBwz0

                // It will helps to load the video and play by manual.
                player?.cueVideo("CzUslqxBwz0")

            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                println("YOU TUBE " + p1.toString())
                Toast.makeText(
                    this@DiscoverDetailsScreenActivity,
                    "Video player Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })



        binding.selfieChallengeInfo.setOnClickListener {

           /* if (poiQuestion == "?") {
                Toast.makeText(applicationContext, "Don't have Question", Toast.LENGTH_SHORT).show()
            } else {*/
                aboutWindow()


                /*if (checkPermission()) {

                    startActivity(Intent(this@DiscoverDetailsScreenActivity, com.breadcrumbsapp.view.arcore.ArCoreUpdateAct::class.java))

                } else {
                    requestPermission()
                }*/
           // }

        }

        binding.takeMeThereBtn.setOnClickListener {

            sharedPreference.saveSession("clicked_button", "take_me_there")
            finish()
        }




    }

    private fun aboutWindow() {
        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.about_selfie_challenge)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setDimAmount(0.5f)
        val okBtn = dialog.findViewById(R.id.okButton) as TextView

        val logoIconView = dialog.findViewById<ImageView>(R.id.logoIconView)
        val challengeTitle = dialog.findViewById<TextView>(R.id.challengeTitle)
        val challengeContent = dialog.findViewById<TextView>(R.id.challengeContent)

        if (challengeName == "quiz") {
            logoIconView.setImageDrawable(getDrawable(R.drawable.quiz_challenge_icon))
            challengeTitle.text = resources.getString(R.string.about_quiz_challenge_title)
            challengeContent.text = resources.getString(R.string.quiz_challenge_content)
        } else if (challengeName == "selfie") {
            logoIconView.setImageDrawable(getDrawable(R.drawable.selfie_challenge_icon))
            challengeTitle.text = resources.getString(R.string.about_selfie_challenge_title)
            challengeContent.text = resources.getString(R.string.selfie_challenge_content)
        }
        okBtn.setOnClickListener {
            dialog.dismiss()
            /* startActivity(Intent(this@DiscoverDetailsScreenActivity, ChallengeActivity::class.java).putExtra("challengeName",challengeName)
                 .putExtra("poiImage",poiImage)
             )*/
        }

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    private fun openScannerScreen() {
        startActivity(
            Intent(
                this@DiscoverDetailsScreenActivity,
                com.breadcrumbsapp.view.qrcode.DecoderActivity::class.java
            ).putExtra("poiQrCode", poiQrCode)
                .putExtra("challengeName", challengeName)
                .putExtra("poiImage", poiImage).putExtra("poiArid", poiArid)
        )

/*
        startActivity(
            Intent(
                this@DiscoverDetailsScreenActivity,
                com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
            )
        )*/
    }

    private fun openCameraClass() {

        // For QR Code Scan
        //  startActivity(Intent(this@DiscoverDetailsScreenActivity, com.breadcrumbsapp.view.qrcode.DecoderActivity::class.java))


        // For AR Screen Updated Class,
       /* startActivity(
            Intent(
                this@DiscoverDetailsScreenActivity,
                com.breadcrumbsapp.ARCoreActivity::class.java
            )
        )*/
        //   startActivity(Intent(this@DiscoverDetailsScreenActivity, HelloSceneformActivity::class.java))
        //startActivity(Intent(this@DiscoverDetailsScreenActivity, SampleARClass::class.java))

            //Needs to delete this class
        //   startActivity(Intent(this@DiscoverDetailsScreenActivity, com.breadcrumbsapp.view.arcore.ArCoreUpdateAct::class.java))
    }


    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //openCameraClass()
                openScannerScreen()
            } else {

                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        showMessageOKCancel(
                            "You need to allow access permission"
                        ) { dialog, which ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@DiscoverDetailsScreenActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }






}