package com.breadcrumbsapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.databinding.SplashScreenBinding
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.view.DiscoverScreenActivity
import com.breadcrumbsapp.view.TutorialActivity
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.splash_screen.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPreference: SessionHandlerClass
    private lateinit var binding:SplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        sharedPreference = SessionHandlerClass(applicationContext)

        //printHashKey(this)

        /*val c = Calendar.getInstance()
        //"2021-10-13 04:12:13"
        val month =c.get(Calendar.MONTH)
        val dateFormat="${c.get(Calendar.YEAR)}-${month+1}-${c.get(Calendar.DAY_OF_MONTH)} ${c.get(Calendar.HOUR_OF_DAY)}:${c.get(
            Calendar.MINUTE)} :${c.get(Calendar.SECOND)}"

        println("Date ::: $dateFormat")*/

        Handler(Looper.getMainLooper()).postDelayed({

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

            }

            // test for marker move..

           /* startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left)
            finish()*/

        }, 1000)


    }


}


fun printHashKey(pContext: Context) {
    try {
        val info: PackageInfo = pContext.packageManager
            .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())  //.encode(md.digest(), 0)
            val hashKey = String(Base64.encode(md.digest(), 0))
           // Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            println("printHashKey() Hash Key: $hashKey")
        }
    } catch (e: NoSuchAlgorithmException) {
        println("printHashKey() Hash Key: $e")
    } catch (e: Exception) {
        println("printHashKey() Hash Key: $e")
    }
}
