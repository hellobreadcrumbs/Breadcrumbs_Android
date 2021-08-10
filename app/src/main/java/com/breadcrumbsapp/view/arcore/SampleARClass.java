package com.breadcrumbsapp.view.arcore;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.breadcrumbsapp.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class SampleARClass extends AppCompatActivity {
    private static final String TAG = SampleARClass.class.getSimpleName();
    Session mSession;
    private ArFragment arFragment;
    private ArSceneView arSceneView;
    private ModelRenderable andyRenderable;
    private boolean modelAdded = false; // add model once

    private boolean sessionConfigured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_layout);
        arFragment= (ArFragment)  getSupportFragmentManager().findFragmentById(R.id.sceneView);

        // hiding the plane discovery
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        arSceneView= arFragment.getArSceneView();

    }

    private boolean setupAugmentedImageDb(Config config) {
        AugmentedImageDatabase augmentedImageDatabase;

        Bitmap augmentedImageBitmap = loadAugmentedImage();
        if (augmentedImageBitmap == null) {
            return false;
        }

        augmentedImageDatabase = new AugmentedImageDatabase(mSession);
        augmentedImageDatabase.addImage("car", augmentedImageBitmap);

        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }


    private Bitmap loadAugmentedImage(){
        try (InputStream is = getAssets().open("car.jpeg")){
            return BitmapFactory.decodeStream(is);
        }
        catch (IOException e){
            Log.e("ImageLoad", "IO Exception while loading", e);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime){
        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage : augmentedImages){
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING){

                if (augmentedImage.getName().contains("car") && !modelAdded){
                    renderObject(arFragment,
                            augmentedImage.createAnchor(augmentedImage.getCenterPose()),
                            R.raw.car);
                    modelAdded = true;
                }
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void renderObject(ArFragment fragment, Anchor anchor, int model){
        ModelRenderable.builder()
                .setSource(this, model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));

    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, ModelRenderable renderable){
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSession != null) {

            arSceneView.pause();
            mSession.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSession == null) {
            String message = null;
            Exception exception = null;
            try {
                mSession = new Session(this);
            } catch (UnavailableArcoreNotInstalledException
                    e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update android";
                exception = e;
            } catch (Exception e) {
                message = "AR is not supported";
                exception = e;
            }

            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
            sessionConfigured = true;

        }
        if (sessionConfigured) {
            configureSession();
            sessionConfigured = false;

            arSceneView.setupSession(mSession);
        }


    }
    private void configureSession() {
        Config config = new Config(mSession);
        if (!setupAugmentedImageDb(config)) {
            Toast.makeText(this, "Unable to setup augmented", Toast.LENGTH_SHORT).show();
        }
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        mSession.configure(config);
    }
}
