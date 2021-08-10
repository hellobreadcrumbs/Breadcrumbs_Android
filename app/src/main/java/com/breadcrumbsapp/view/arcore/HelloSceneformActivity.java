package com.breadcrumbsapp.view.arcore;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.breadcrumbsapp.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
    private static final String TAG = HelloSceneformActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.1;

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private Button btnLine;
    private AnchorNode anchorNode;
    private AnchorNode mAnchorNode;

    @RequiresApi(api = VERSION_CODES.N)
    @Override @SuppressWarnings({ "AndroidApiChecker", "FutureReturnValueIgnored" })
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.ar_layout);
       /* btnLine = findViewById(R.id.btnLine);
        btnLine.setOnClickListener(v -> {
            onUpdateRender();
        });*/

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment_view);

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(getApplicationContext(), R.raw.model)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast toast = Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return null;
                });
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.1 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString = ((ActivityManager) activity.getSystemService(
                Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.1 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.1 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

  /*  private void onUpdateRender() {
        arFragment.getArSceneView().getScene().setOnUpdateListener(this::onUpdate);
    }*/

    public void onUpdate(FrameTime frameTime) {
        if (arFragment.getArSceneView().getArFrame() == null) {
            Log.d(TAG, "onUpdate: No frame available");
            // No frame available
            return;
        }

        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);

        if (arFragment.getArSceneView().getArFrame().getCamera().getTrackingState()
                != TrackingState.TRACKING) {
            Log.d(TAG, "onUpdate: Tracking not started yet");
            // Tracking not started yet
            return;
        }

        if (this.mAnchorNode == null && andyRenderable != null) {
            Log.d(TAG, "onUpdate: mAnchorNode is null");
            Session session = arFragment.getArSceneView().getSession();

            /*float[] position = {0, 0, -1};
            float[] rotation = {0, 0, 0, 1};
            Anchor anchor = session.createAnchor(new Pose(position, rotation));*/

            Vector3 cameraPos = arFragment.getArSceneView().getScene().getCamera().getWorldPosition();
            Vector3 cameraForward = arFragment.getArSceneView().getScene().getCamera().getForward();
            Vector3 position = Vector3.add(cameraPos, cameraForward.scaled(1.0f));

            // Create an ARCore Anchor at the position.
            Pose pose = Pose.makeTranslation(position.x, position.y, position.z);
            Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pose);

            mAnchorNode = new AnchorNode(anchor);
            mAnchorNode.setParent(arFragment.getArSceneView().getScene());

     /* Node node = new Node();
      node.setRenderable(andyRenderable);
      node.setParent(mAnchorNode);
      node.setOnTapListener((hitTestResult, motionEvent) -> {
      });*/

            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
            transformableNode.setRenderable(andyRenderable);
            transformableNode.setParent(mAnchorNode);
        }
    }
}

