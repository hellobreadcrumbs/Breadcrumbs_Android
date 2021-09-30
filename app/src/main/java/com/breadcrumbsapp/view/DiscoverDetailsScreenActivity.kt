package com.breadcrumbsapp.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
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
import com.breadcrumbsapp.view.arcore.ARCoreActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.DiscoverDetailsScreenBinding
import com.breadcrumbsapp.model.GetEventsModel
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.discover_details_screen.*
import kotlinx.android.synthetic.main.discover_details_screen.detailsPoiBackGround
import kotlinx.android.synthetic.main.leader_board_activity_layout.*
import kotlinx.android.synthetic.main.quiz_challenge.*
import kotlinx.android.synthetic.main.quiz_challenge_question_activity.*
import java.util.*


class DiscoverDetailsScreenActivity : YouTubeBaseActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
    }


    private lateinit var sharedPreference: SessionHandlerClass
    // private val youtubeApiKey =  "AIzaSyCxE4GNw8TJQOoYi6gebWcJ3KtUCTlCduE"
    private val youtubeApiKey = "AIzaSyDrMQduXjWkxg3nfqxXGiUCvpJTV84DCto"
    lateinit var binding: DiscoverDetailsScreenBinding
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
    private var challengeName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DiscoverDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SessionHandlerClass(applicationContext)


        val bundle: Bundle? = intent.extras
        val from = bundle!!.getString("from")

        poiName = sharedPreference.getSession("selectedPOIName").toString()
        poiID = sharedPreference.getSession("selectedPOIID").toString()
        poiDistance = sharedPreference.getSession("poiDistance").toString()
        poiImage = sharedPreference.getSession("selectedPOIImage").toString()
        poiETA = sharedPreference.getSession("selectedPOIDuration").toString()
        poiQuestion = sharedPreference.getSession("selectedPOIQuestion").toString()
        poiHint = sharedPreference.getSession("selectedPOIHintContent").toString()
        poiChType = sharedPreference.getSession("selectedPOIChallengeType").toString()
        poiArid = sharedPreference.getSession("selectedPOIARid").toString()
        poiQrCode = sharedPreference.getSession("selectedPOIQrCode").toString()

        println("poiQrCode $poiQrCode")
        println("poiChType $poiChType")
        println("poiArid $poiArid")


        discover_detail_screen_trailName.text=sharedPreference.getSession("selected_trails")
        if(sharedPreference.getSession("selected_trail_id")=="4")
        {
            Glide.with(applicationContext).load(R.drawable.breadcrumbs_trail).into(discover_detail_screen_trail_icon)
        }
        else if(sharedPreference.getSession("selected_trail_id")=="6")
        {
            Glide.with(applicationContext).load(R.drawable.anthology_trail_icon).into(discover_detail_screen_trail_icon)
        }


        // poiChType = 1 means Quiz and 0 means Selfie

        if (poiArid == "1") {
            // needs open AR screen
            challengeName = "ar_screen"

            challengeNameTv.text = resources.getText(R.string.ar_discovery_title)  // Changed on Aug'9

            Glide.with(applicationContext).load(R.drawable.ar_challenge_icon).into(challenge_image)
            Glide.with(applicationContext).load(R.drawable.details_screen_ar_icon).into(scannerIcon)
        } else {

            if (poiChType == "0") {
                challengeName = "selfie"
                challengeNameTv.text = resources.getText(R.string.selfie_challenge_title)
                Glide.with(applicationContext).load(R.drawable.selfie_challenge_icon)
                    .into(challenge_image)
                Glide.with(applicationContext).load(R.drawable.details_screen_camera)
                    .into(scannerIcon)
            } else {
                challengeName = "quiz"
                challengeNameTv.text = resources.getText(R.string.quiz_challenge_title)
                Glide.with(applicationContext).load(R.drawable.quiz_challenge_icon)
                    .into(challenge_image)
                Glide.with(applicationContext).load(R.drawable.details_screen_qr_scan)
                    .into(scannerIcon)
            }
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


            Glide.with(applicationContext)
                .load(poiImage)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        poi_details_loader.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        poi_details_loader.visibility = View.GONE
                        return false
                    }
                })
                .into(poiImageView)

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
                binding.takeMeThereBtn.text = resources.getText(R.string.arrived_text)
                binding.takeMeThereBtn.background = getDrawable(R.drawable.arrived_btn)
                binding.discoverStatusText.text = resources.getText(R.string.discover_status_text_undiscovered)
            }
            from == "DISCOVERED" -> {
                binding.scannerIcon.alpha = 0.5f
                binding.takeMeThereBtn.text = resources.getText(R.string.take_me_there)
                binding.takeMeThereBtn.background = getDrawable(R.drawable.take_me_there_bg)
                binding.discoverStatusText.text = resources.getText(R.string.discover_status_text_discovered)

                detailsPoiBackGround.background = getDrawable(R.drawable.trail_banner_discovered)
            }
        }

        binding.cameraIconLayout.setOnClickListener {


            if (from.equals("take_me_there")) {

                // do nothing

            } else if (from.equals(resources.getString(R.string.discover))) {

                if (checkPermission()) {

                    openQRCODEScannerScreen()

                } else {
                    requestPermission()
                }

            }

        }


        binding.backButton.setOnClickListener {

            sharedPreference.saveSession("clicked_button", "from_back_button")
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



        breadcrumbsTrailInfoBtn.setOnClickListener(View.OnClickListener {
            trailInfoWindow()
        })
        binding.selfieChallengeInfo.setOnClickListener {

            aboutWindow()

        }

        binding.takeMeThereBtn.setOnClickListener {

            sharedPreference.saveSession("clicked_button", "take_me_there")
            sharedPreference.saveSession("toggle_button", "map")
            finish()
        }


    }
    private fun trailInfoWindow() {
        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.about_trail)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setDimAmount(0.5f)
        val okBtn = dialog.findViewById(R.id.okButton) as TextView
        val trailName=dialog.findViewById(R.id.about_screen_trail_name) as TextView
        val trailIcon=dialog.findViewById(R.id.about_screen_trail_icon) as ImageView
//
        val trailContent=dialog.findViewById(R.id.about_trail_content) as TextView

        trailName.text=discover_detail_screen_trailName.text.toString()
        trailContent.text="Hanse the cat needs your help on a quest to rescue his friend Grey the pigeon, who was kidnapped by a mysterious witch. The explorer will need to follow the trail of breadcrumbs with Hanse to find and save Grey before it's too late."


        if(sharedPreference.getSession("selected_trail_id")=="4")
        {
            Glide.with(applicationContext).load(R.drawable.wild_about_twlight_icon).into(trailIcon)


        }
        else if(sharedPreference.getSession("selected_trail_id")=="6")
        {
            Glide.with(applicationContext).load(R.drawable.anthology_trail_icon).into(trailIcon)

        }


        okBtn.setOnClickListener {
            dialog.dismiss()

        }

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
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

        when (challengeName) {
            "quiz" -> {

                Glide.with(applicationContext).load(R.drawable.quiz_challenge_icon).into(logoIconView)
                challengeTitle.text = resources.getString(R.string.about_quiz_challenge_title)
                challengeContent.text = resources.getString(R.string.about_quiz_challenge_info_content)
            }
            "selfie" -> {

                Glide.with(applicationContext).load(R.drawable.selfie_challenge_icon).into(logoIconView)
                challengeTitle.text = resources.getString(R.string.about_selfie_challenge_title)
                challengeContent.text = resources.getString(R.string.about_selfie_challenge_info_content)
            }
            "ar_screen" -> {

                Glide.with(applicationContext).load(R.drawable.ar_challenge_icon).into(logoIconView)
                challengeTitle.text = resources.getString(R.string.about_mystery_challenge_title)
                challengeContent.text =
                    resources.getString(R.string.about_mystery_challenge_info_content)
            }
        }
        okBtn.setOnClickListener {
            dialog.dismiss()

        }

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    private fun openQRCODEScannerScreen() {

        // If challenge Quiz means, needs to show QR code .,
        // IF challenge Selfie & AR means, needs to skip QR code...

        if(challengeName == "quiz")
        {

            startActivity(
                Intent(
                    this@DiscoverDetailsScreenActivity,
                    com.breadcrumbsapp.view.qrcode.DecoderActivity::class.java
                ).putExtra("poiQrCode", poiQrCode)
                    .putExtra("challengeName", challengeName)
                    .putExtra("poiImage", poiImage).putExtra("poiArid", poiArid)
            )
            finish()
        }
        else
        {
            if (poiArid == "1") {
                startActivity(Intent(this@DiscoverDetailsScreenActivity, ARCoreActivity::class.java))
                finish()
            } else {
                startActivity(
                    Intent(
                        this@DiscoverDetailsScreenActivity,
                        ChallengeActivity::class.java
                    ).putExtra("challengeName", challengeName)
                        .putExtra("poiImage", poiImage).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                finish()
            }
        }

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
                openQRCODEScannerScreen()
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

    override fun onBackPressed() {
      //  super.onBackPressed()
        sharedPreference.saveSession("clicked_button", "from_back_button")
        finish()
    }


}