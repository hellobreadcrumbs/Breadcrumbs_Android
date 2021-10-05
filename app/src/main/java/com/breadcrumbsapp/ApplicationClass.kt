package com.breadcrumbsapp

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.multidex.MultiDexApplication


class ApplicationClass : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    private lateinit var mActivity: Activity
    lateinit var dialog: Dialog
    private var isGpsEnabled = false
    private var isNetworkEnabled = false
    var locationDialog: Dialog?=null


    private val networkChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MainActivity", "onReceive: ")


            if(!isInternetAvailable(context))
            {
                noInternetConnectionDialog()
            }

            else
            {
                if (dialog != null) {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                }
            }



        }
    }



    private val locationStateChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MainActivity", "onReceive: ")


            if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {

                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


                println(" $isGpsEnabled => $isNetworkEnabled")
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
    }

    fun isInternetAvailable(context: Context): Boolean {
        val conMgr = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = conMgr.activeNetwork
            val networkCapabilities = conMgr.getNetworkCapabilities(network)
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        } else {
            // below API Level 23
            (conMgr.activeNetworkInfo != null && conMgr.activeNetworkInfo!!.isAvailable
                    && conMgr.activeNetworkInfo!!.isConnected)
        }
    }

    private fun noInternetConnectionDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connection_layout)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setDimAmount(0.5f)
        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    private fun noGPSLocationDialog(boolValue: Boolean) {

        try {
            if(locationDialog==null)
            {
                locationDialog = Dialog(mActivity, R.style.FirebaseUI_Transparent)
                locationDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)

            }

            locationDialog!!.setCancelable(false)
            locationDialog!!.setContentView(R.layout.gps_indiaction_layout)
            locationDialog!!.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            locationDialog!!.window?.setDimAmount(0.5f)
            locationDialog!!.window!!.attributes!!.windowAnimations = R.style.DialogTheme
            if (boolValue) {

                println("Application Class => noGPSLocationDialog => $boolValue")
                locationDialog!!.show()
            } else {
                locationDialog!!.dismiss()
                println("Application Class => noGPSLocationDialog => $boolValue ==> ${locationDialog!!.isShowing}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()

        registerActivityLifecycleCallbacks(this)

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        mActivity = activity
        dialog = Dialog(mActivity, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

       // val br: BroadcastReceiver = locationStateChangeReceiver
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(locationStateChangeReceiver, filter)

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)

    }

    override fun onActivityResumed(activity: Activity) {

        mActivity = activity
        dialog = Dialog(mActivity, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // val br: BroadcastReceiver = locationStateChangeReceiver
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(locationStateChangeReceiver, filter)

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)

    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity)
    {
        unregisterReceiver(networkChangeReceiver)
        unregisterReceiver(locationStateChangeReceiver)
    }

}