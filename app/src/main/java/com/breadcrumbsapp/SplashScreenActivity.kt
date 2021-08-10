package com.breadcrumbsapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri.encode
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.DiscoverScreenActivity
import com.breadcrumbsapp.view.TutorialActivity
import com.google.firebase.FirebaseApp
import java.net.URLEncoder.encode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPreference: SessionHandlerClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash_screen)

       // setContentView(R.layout.splash_screen)
        FirebaseApp.initializeApp(this)
        // myReceiver=BreadcrumbsReceiver()
        sharedPreference = SessionHandlerClass(applicationContext)

        printHashKey(this)


        Handler(Looper.getMainLooper()).postDelayed({
           // startActivity(Intent(this@SplashScreenActivity, LoginScreenActivity::class.java))



            val isLogin:Boolean =sharedPreference.getBoolean("isLogin")
            println("isLogin = $isLogin")
            if(isLogin)
            {

                startActivity(Intent(this@SplashScreenActivity, DiscoverScreenActivity::class.java).putExtra("isFromLogin","no"))
                overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left)
                finish()

            }
            else
            {

                startActivity(Intent(this@SplashScreenActivity, TutorialActivity::class.java))
                overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left)
                finish()
             /*   startActivity(Intent(this@SplashScreenActivity, TutorialActivity::class.java))
                overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left)
                finish()*/
            }

        }, 1500)


    }



}

fun printHashKey(pContext: Context) {
    try {
        val info: PackageInfo = pContext.getPackageManager()
            .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())  //.encode(md.digest(), 0)
            val hashKey: String = String(Base64.encode(md.digest(), 0))
           // Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            println("printHashKey() Hash Key: $hashKey")
        }
    } catch (e: NoSuchAlgorithmException) {
        println("printHashKey() Hash Key: $e")
    } catch (e: Exception) {
        println("printHashKey() Hash Key: $e")
    }
}

/*   private fun configureReceiver() {
         println("********** configureReceiver")



         val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
         val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

         Toast.makeText(this, "Internet : $isConnected",Toast.LENGTH_LONG).show()

         if(isConnected)
         {

             Handler(Looper.getMainLooper()).postDelayed({
                 startActivity(Intent(this@SplashScreenActivity, LoginScreenActivity::class.java))
                 finish()
             }, 3000)
         }

         else
         {
             internetAlert()
         }
     }*/