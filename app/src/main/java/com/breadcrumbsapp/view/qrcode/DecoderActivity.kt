package com.breadcrumbsapp.view.qrcode

import android.Manifest
import android.content.Intent

import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import com.breadcrumbsapp.ARCoreActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.view.ChallengeActivity
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener
import com.google.android.material.snackbar.Snackbar


class DecoderActivity : AppCompatActivity(), OnRequestPermissionsResultCallback,
    OnQRCodeReadListener {

    private var qrCodeReaderView: QRCodeReaderView? = null
    private var pointsOverlayView: PointsOverlayView? = null
    private var flashlightToggleBtn: ToggleButton? = null
    private var mainLayout: ViewGroup? = null
    private var backButton: Button? = null
    private var poiQrCode = ""
    private var challengeName = ""
    private var poiImage = ""
    private var poiArId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decoder)
        mainLayout = findViewById<View>(R.id.main_layout) as ViewGroup

        val bundle: Bundle? = intent.extras
        poiQrCode = bundle!!.getString("poiQrCode").toString()
        challengeName = bundle.getString("challengeName").toString()
        poiImage = bundle.getString("poiImage").toString()
        poiArId = bundle.getString("poiArid").toString()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initQRCodeReaderView()
        } else {
            requestCameraPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (qrCodeReaderView != null) {
            qrCodeReaderView!!.startCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        if (qrCodeReaderView != null) {
            qrCodeReaderView!!.stopCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return
        }
        if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout!!, "Camera permission was granted.", Snackbar.LENGTH_SHORT)
                .show()
            initQRCodeReaderView()
        } else {
            Snackbar.make(
                mainLayout!!,
                "Camera permission request was denied.",
                Snackbar.LENGTH_SHORT
            )
                .show()
        }
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    override fun onQRCodeRead(text: String, points: Array<PointF>) {

        pointsOverlayView!!.setPoints(points)

        println("poiQrCode:: $poiQrCode == $text")
        println("poiArId:: $poiArId" )
        if (poiQrCode == text) {
            if(poiArId=="1")
            {
                startActivity(Intent(this@DecoderActivity, ARCoreActivity::class.java))

            }
            else
            {
                startActivity(
                    Intent(
                        this@DecoderActivity,
                        ChallengeActivity::class.java
                    ).putExtra("challengeName", challengeName)
                        .putExtra("poiImage", poiImage)
                )
            }

        } else {
            Toast.makeText(applicationContext, "Please Try Again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(
                mainLayout!!, "Camera access is required to display the camera preview.",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("OK") {
                ActivityCompat.requestPermissions(
                    this@DecoderActivity, arrayOf(
                        Manifest.permission.CAMERA
                    ), MY_PERMISSION_REQUEST_CAMERA
                )
            }.show()
        } else {
            Snackbar.make(
                mainLayout!!, "Permission is not available. Requesting camera permission.",
                Snackbar.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.CAMERA
                ), MY_PERMISSION_REQUEST_CAMERA
            )
        }
    }

    private fun initQRCodeReaderView() {
        val content: View = layoutInflater.inflate(R.layout.content_decoder, mainLayout, true)
        qrCodeReaderView = content.findViewById<View>(R.id.qrdecoderview) as QRCodeReaderView
        flashlightToggleBtn =
            content.findViewById<View>(R.id.flashButtonContentScreen) as ToggleButton
        pointsOverlayView =
            content.findViewById<View>(R.id.points_overlay_view) as PointsOverlayView
        backButton =
            content.findViewById<View>(R.id.backButton_content_decoder_screen) as Button
        qrCodeReaderView!!.setAutofocusInterval(2000L)
        qrCodeReaderView!!.setOnQRCodeReadListener(this)
        qrCodeReaderView!!.setBackCamera()
        qrCodeReaderView!!.setQRDecodingEnabled(true)
        qrCodeReaderView!!.startCamera()


        backButton!!.setOnClickListener {

            finish()
        }


        flashlightToggleBtn!!.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                qrCodeReaderView!!.setTorchEnabled(true)

            } else {
                qrCodeReaderView!!.setTorchEnabled(false)
            }
        }

    }

    companion object {
        private const val MY_PERMISSION_REQUEST_CAMERA = 0
    }
}