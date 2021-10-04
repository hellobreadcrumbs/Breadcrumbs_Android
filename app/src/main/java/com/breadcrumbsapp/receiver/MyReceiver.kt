package com.breadcrumbsapp.receiver

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.breadcrumbsapp.R


class MyReceiver(activity: Activity) : BroadcastReceiver() {
    private var isGpsEnabled = false
    private var isNetworkEnabled = false
     var dialog: Dialog?=null
    var mActivity: Activity = activity

    // START OF onReceive
    override fun onReceive(context: Context, intent: Intent) {

        // PRIMARY RECEIVER
        if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
            Log.i(TAG, "Location Providers Changed")
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


            println("$TAG => $isGpsEnabled => $isNetworkEnabled")
            if (!isGpsEnabled || !isNetworkEnabled) {
                try {

                    noGPSLocationDialog(true)


                } catch (e: Exception) {
                    e.printStackTrace()

                }
            } else {
                noGPSLocationDialog(false)
            }

        }

    }

    private fun noGPSLocationDialog(boolValue: Boolean) {

        try {
            if(dialog==null)
            {
                dialog = Dialog(mActivity, R.style.FirebaseUI_Transparent)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)

            }

            dialog!!.setCancelable(false)
            dialog!!.setContentView(R.layout.gps_indiaction_layout)
            dialog!!.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog!!.window?.setDimAmount(0.5f)
            dialog!!.window!!.attributes!!.windowAnimations = R.style.DialogTheme
            if (boolValue) {

                println("LocationProviderChanged => noGPSLocationDialog => $boolValue")
                dialog!!.show()
            } else {
                dialog!!.dismiss()
                println("LocationProviderChanged => noGPSLocationDialog => $boolValue ==> ${dialog!!.isShowing}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        private const val TAG = "LocationProviderChanged"
    }


}