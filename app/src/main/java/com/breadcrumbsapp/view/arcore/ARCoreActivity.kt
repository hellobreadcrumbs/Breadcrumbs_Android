package com.breadcrumbsapp.view.arcore

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.breadcrumbsapp.databinding.ArLayoutBinding
import com.breadcrumbsapp.util.SessionHandlerClass
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.ar_layout.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ARCoreActivity : AppCompatActivity() {
    //lateinit for Augmented Reality Fragment
    private lateinit var arFragment: ArFragment

    //lateinit for the model uri
    private lateinit var selectedObject: Uri
    lateinit var binding: ArLayoutBinding

    lateinit var sharedPreferences: SessionHandlerClass

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = SessionHandlerClass(applicationContext)
        //Init Fragment
        arFragment =
            supportFragmentManager.findFragmentById(binding.sceneformFragmentView.id) as ArFragment

        //Default model
       // setModelPath("model.sfb")
        setModelPath("gingerbread_house.sfb")



        //Tab listener for the ArFragment
        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            //If surface is not horizontal and upward facing
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                //return for the callback
                return@setOnTapArPlaneListener
            }
            //create a new anchor
            val anchor = hitResult.createAnchor()
            placeObject(arFragment, anchor, selectedObject)
        }

        /* //Click listener for lamp and table objects
         binding.smallTable.setOnClickListener {
             setModelPath("model.sfb")
         }
         binding.bigLamp.setOnClickListener {
             setModelPath("LampPost.sfb")
         }*/
        binding.captureBtn.setOnClickListener {

            arFragment.arSceneView.planeRenderer.isVisible = false

            Handler(Looper.getMainLooper()).postDelayed(
                { takePhoto() }, 100
            )
        }


    }

    fun getBitmapOFRootView(v: android.view.View): Bitmap? {
        val rootview: android.view.View = v.rootView
        rootview.isDrawingCacheEnabled = true
        return rootview.drawingCache
    }

    /***
     * function to handle the renderable object and place object in scene
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun placeObject(fragment: ArFragment, anchor: Anchor, modelUri: Uri) {
        val modelRenderable = ModelRenderable.builder()
            .setSource((fragment.requireContext()), modelUri)
            .build()
        //when the model render is build add node to scene
        modelRenderable.thenAccept { renderableObject ->
            addNodeToScene(
                fragment,
                anchor,
                renderableObject
            )
        }
        //handle error
        modelRenderable.exceptionally {
            val toast = Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
            toast.show()
            null
        }
    }

    /***
     * Function to a child anchor to a new scene.
     */
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderableObject: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderableObject
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

    /***
     * function to get the model resource on assets directory for each figure.
     */
    private fun setModelPath(modelFileName: String) {
        selectedObject = Uri.parse(modelFileName)
        val toast = Toast.makeText(applicationContext, modelFileName, Toast.LENGTH_SHORT)
        //toast.show()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun takePhoto() {
        val filename = generateFilename()
        val view: ArSceneView = arFragment.arSceneView

        // Create a bitmap the size of the scene view.
        val bitmap = Bitmap.createBitmap(
            view.width, view.height,
            Bitmap.Config.ARGB_8888
        )

        binding.imageView.visibility = View.GONE
        binding.imageView.setImageBitmap(bitmap)
        println("Bitmap = $bitmap")


        // Create a handler thread to offload the processing of the image.
        val handlerThread = HandlerThread("PixelCopier")
        handlerThread.start()
        // Make the request to copy.
        PixelCopy.request(view, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    if (filename != null && bitmap != null) {
                        //saveBitmapToDisk(bitmap, filename)


                        val file = File(
                            applicationContext.cacheDir,
                            "CUSTOM NAME"
                        ) //Get Access to a local file.
                        file.delete() // Delete the File, just in Case, that there was still another File
                        file.createNewFile()
                        val fileOutputStream = file.outputStream()
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        val bytearray = byteArrayOutputStream.toByteArray()
                        fileOutputStream.write(bytearray)
                        fileOutputStream.flush()
                        fileOutputStream.close()
                        byteArrayOutputStream.close()
                        val URI = file.toURI()
                        sharedPreferences.saveSession("arURI", URI.toString())
                        startActivity(Intent(applicationContext, ARImagePostScreen::class.java))
                    } else {
                        println("File Name is NULL $bitmap")
                    }
                } catch (e: Exception) {
                    val toast: Toast = Toast.makeText(
                        this@ARCoreActivity, e.toString(),
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                    return@request
                }
                @SuppressLint("WrongConstant") val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Photo saved", Snackbar.LENGTH_LONG
                )
                snackbar.setAction("Open in Photos") { v: View? ->
                    val photoFile = File(filename)
                    val photoURI = FileProvider.getUriForFile(
                        this@ARCoreActivity,
                        this@ARCoreActivity.packageName
                            .toString() + ".ar.codelab.name.provider",
                        photoFile
                    )
                    val intent = Intent(Intent.ACTION_VIEW, photoURI)
                    intent.setDataAndType(photoURI, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
                snackbar.show()
            } else {
                val toast = Toast.makeText(
                    this@ARCoreActivity,
                    "Failed to copyPixels: $copyResult", Toast.LENGTH_LONG
                )
                toast.show()
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))


    }

    private fun saveBitmapToDisk(bitmap: Bitmap, filename: String) {
        val root = Environment.getExternalStorageDirectory().toString() + "/BR-AR"
        Log.d("saveBitmapToDisk", "saveBitmapToDisk: $root")
        val myDir = File(root)
        myDir.mkdirs()
        val file = File(myDir, filename)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException("Failed to save bitmap to disk", e)
        }
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()), null
        ) { path: String, uri: Uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    private fun generateFilename(): String? {
        val date: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return date + "_ar_app.jpg"
    }


}
