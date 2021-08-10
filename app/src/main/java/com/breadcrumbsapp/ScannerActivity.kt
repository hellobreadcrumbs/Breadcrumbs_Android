package com.breadcrumbsapp

import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.ScanScreenBinding
import com.budiyev.android.codescanner.*


class ScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    lateinit var binding: ScanScreenBinding
     lateinit var mCameraId: String
     lateinit var cameraManager: CameraManager
     lateinit var captureSession: CameraCaptureSession


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScanScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isFlashAvailable = applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)

        if (!isFlashAvailable) {
            showNoFlashError()
        }


         cameraManager =   getSystemService(CAMERA_SERVICE) as CameraManager
         mCameraId = cameraManager.cameraIdList[0]


        binding.backButton.setOnClickListener {
            finish()
        }

        binding.flashButton.setOnCheckedChangeListener { _, isChecked ->
          //
        }


        // QR CODE SCANNER CONCEPT...
        codeScanner = CodeScanner(applicationContext, binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = true // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                  Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
               // println("Scan result: ${it.text}")
                finish()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                //Toast.makeText(this, "Camera initialization error: ${it.message}",Toast.LENGTH_LONG).show()
                println("Scan result ERROR : ${it.message}")
            }
        }
        // QR CODE CONCEPT COMPLETED


    }




    private fun showNoFlashError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Oops!")
        builder.setMessage("Flash not available in this device...")


        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }


        builder.show()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}