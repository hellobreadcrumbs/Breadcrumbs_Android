package com.breadcrumbsapp.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.SettingsActivityBinding

import com.breadcrumbsapp.util.SessionHandlerClass
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsScreenAct : AppCompatActivity() {
    private lateinit var sessionHandlerClass: SessionHandlerClass
    private lateinit var binding: SettingsActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionHandlerClass = SessionHandlerClass(applicationContext)




        setonClickListeners()
    }

    private fun setonClickListeners() {

        vibration_layout.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext,"Under Construction",Toast.LENGTH_SHORT).show()
        })
        sign_out_layout.setOnClickListener {
            signOutAlertWindow()
        }

        user_info_layout.setOnClickListener {

            startActivity(Intent(this@SettingsScreenAct, UserInformationActivity::class.java))
        }

        settings_screen_backButton.setOnClickListener {
            finish()
        }

        privacy_policy_layout.setOnClickListener {
            startActivity(
                Intent(
                    this@SettingsScreenAct,
                    SettingsWebViewActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("terms", "no")
            )


        }

        terms_of_service_layout.setOnClickListener {
            startActivity(
                Intent(
                    this@SettingsScreenAct,
                    SettingsWebViewActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("terms", "yes")
            )
        }

        faq_layout.setOnClickListener {

            startActivity(Intent(this@SettingsScreenAct, FAQActivity::class.java))
        }
    }

    private fun signOutAlertWindow() {
        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.sign_out_confirmation_layout)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setDimAmount(0.5f)
        val okBtn = dialog.findViewById(R.id.sign_out_button) as TextView

        okBtn.setOnClickListener {

            Toast.makeText(applicationContext,"Under Construction",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            /*try {
                LoginScreen.mGoogleSignInClient.signOut().addOnCompleteListener(OnCompleteListener {
                    println("Clear Session Before: ${sessionHandlerClass.getBoolean("isLogin")}")
                    sessionHandlerClass.clearSession()
                    println("Clear Session Before After: ${sessionHandlerClass.getBoolean("isLogin")}")

                    startActivity(Intent(this@SettingsScreenAct, LoginScreen::class.java))
                    finish()
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }*/
        }

        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()
    }
}