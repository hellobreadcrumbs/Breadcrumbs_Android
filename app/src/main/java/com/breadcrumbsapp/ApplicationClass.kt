package com.breadcrumbsapp

import android.app.Activity
import android.app.AppOpsManager
import android.app.Application
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.multidex.MultiDexApplication
import com.breadcrumbsapp.receiver.MyReceiver


class ApplicationClass : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    private lateinit var mActivity: Activity
    lateinit var dialog: Dialog

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


        val br: BroadcastReceiver = MyReceiver(mActivity)
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(br, filter)

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)



    }

    override fun onActivityResumed(activity: Activity) {
        mActivity = activity

        val br: BroadcastReceiver = MyReceiver(mActivity)
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(br, filter)

        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onActivityPaused(activity: Activity) {
       unregisterReceiver(networkChangeReceiver)
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

}