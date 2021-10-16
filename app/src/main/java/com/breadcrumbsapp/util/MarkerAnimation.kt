package com.breadcrumbsapp.util

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Property
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import com.breadcrumbsapp.interfaces.LatLngInterpolator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


object MarkerAnimation {

    fun animateMarkerToGB(
        marker: Marker,
        finalPosition: LatLng?,
        latLngInterpolator: LatLngInterpolator, maps : GoogleMap
    ) {
        val startPosition: LatLng = marker.position
        val handler = Handler(Looper.getMainLooper())
        val start: Long = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 3000f
        println("Check:::::::::::::::::::: currentLocationMarker animateMarkerToGB ")
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                val pos = latLngInterpolator.interpolate(v, startPosition, finalPosition!!)
                marker.position = pos
                maps.moveCamera(CameraUpdateFactory.newLatLng(pos))
                maps.animateCamera(CameraUpdateFactory.zoomTo(19.0f))

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun animateMarkerToHC(
        marker: Marker,
        finalPosition: LatLng?,
        latLngInterpolator: LatLngInterpolator
    ) {
        val startPosition: LatLng = marker.getPosition()
        val valueAnimator = ValueAnimator()
        valueAnimator.addUpdateListener { animation ->
            val v = animation.animatedFraction
            val newPosition: LatLng =
                latLngInterpolator.interpolate(v, startPosition, finalPosition!!)
            marker.setPosition(newPosition)
        }
        valueAnimator.setFloatValues(0f, 1f) // Ignored.
        valueAnimator.duration = 3000
        valueAnimator.start()
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun animateMarkerToICS(
        marker: Marker?,
        finalPosition: LatLng?,
        latLngInterpolator: LatLngInterpolator
    ) {
        val typeEvaluator: TypeEvaluator<LatLng?> =
            TypeEvaluator<LatLng?> { fraction, startValue, endValue ->
                latLngInterpolator.interpolate(
                    fraction,
                    startValue,
                    endValue
                )
            }
        val property: Property<Marker, LatLng> =
            Property.of(Marker::class.java, LatLng::class.java, "position")
        val animator: ObjectAnimator =
            ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
        animator.duration = 3000
        animator.start()
    }
}