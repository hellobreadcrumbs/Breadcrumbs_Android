package com.breadcrumbsapp.view.arcore

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import kotlinx.android.synthetic.main.ar_scene_layout.*
@RequiresApi(Build.VERSION_CODES.N)
class ArCoreUpdateAct  : AppCompatActivity() {

    private lateinit var scene: Scene
    private lateinit var node: Node

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ar_scene_layout)

        scene = sceneView.scene
        render(Uri.parse("model.sfb"))
    }


    private fun render(uri: Uri) {
        ModelRenderable.builder()
            .setSource(this, uri)
            .build()
            .thenAccept {
                addNode(it)
            }
            .exceptionally {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                return@exceptionally null
            }

    }

    private fun addNode(model: ModelRenderable?) {
        model?.let {
            node = Node().apply {
                setParent(scene)
                localPosition = Vector3(0f, -2f, -7f)
                localScale = Vector3(3f, 3f, 3f)
                renderable = it
            }

            scene.addChild(node)
        }
    }

    override fun onPause() {
        super.onPause()
        sceneView.pause()
    }

    override fun onResume() {
        super.onResume()
        sceneView.resume()
    }
}