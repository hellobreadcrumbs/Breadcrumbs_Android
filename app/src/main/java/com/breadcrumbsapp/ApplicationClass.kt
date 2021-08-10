package com.breadcrumbsapp

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.multidex.MultiDexApplication


class ApplicationClass : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    private lateinit var mActivity: Activity
    lateinit var  dialog:Dialog

    private val networkChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

            val toast:Toast=Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            println("Receiver = " + getConnectionType(context))
              dialog = Dialog(context,R.style.FirebaseUI_Transparent)
            if (getConnectionType(context) == 0) {
               // noInternetConnectionDialog()
                toast.show()
            } else {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
                toast.cancel()
                // do nothing..
                //  Toast.makeText(context,"Connected!",Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun noInternetConnectionDialog() {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.internet_connection_layout)
        dialog.window?.setDimAmount(23.0f)


        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
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
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onActivityResumed(activity: Activity) {
        mActivity = activity
        val intentFilter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onActivityPaused(activity: Activity) {
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}


    fun getConnectionType(context: Context): Int {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return 2
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return 1
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                            return 3
                        }
                        else -> return 0
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    when (type) {
                        ConnectivityManager.TYPE_WIFI -> {
                            return 2
                        }
                        ConnectivityManager.TYPE_MOBILE -> {
                            return 1
                        }
                        ConnectivityManager.TYPE_VPN -> {
                            return 3
                        }
                        else -> return 0
                    }
                }
            }
        }
        return 0
    }
}