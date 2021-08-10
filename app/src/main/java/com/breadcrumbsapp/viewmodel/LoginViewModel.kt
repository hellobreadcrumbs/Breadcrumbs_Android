package com.breadcrumbsapp.viewmodel

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.breadcrumbsapp.R
import com.breadcrumbsapp.view.LoginScreen
import com.breadcrumbsapp.view.WebViewActivity


class LoginViewModel : ViewModel() {

    fun webViewForPrivacy(context: Context) {
        context.startActivity(
            Intent(context, WebViewActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra("urlString", context.getString(R.string.privacy_policy_url))
        )
    }

    fun webViewForTerms(context: Context) {
        context.startActivity(
            Intent(context, WebViewActivity::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
                .putExtra("urlString", context.getString(R.string.terms_of_url))
        )
    }

    fun alertBeforeSignIn(
        flag: Int,
        context: Context
    ) {

        // T&C Content with Link
        val termString = SpannableString(context.getString(R.string.alert_view_terms_content))
        val termsSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#79856C")
            }

            override fun onClick(widget: View) {
                widget.setOnClickListener {
                    webViewForTerms(context)
                    widget.invalidate()
                }
            }
        }
        termString.setSpan(termsSpan, 31, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        // Privacy Content with link
        val privacyString = SpannableString(context.getString(R.string.alert_view_privacy_policy))
        val privacySpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#79856C")
            }

            override fun onClick(widget: View) {
                widget.setOnClickListener {
                    webViewForPrivacy(context)
                    widget.invalidate()
                }
            }
        }
        privacyString.setSpan(privacySpan, 8, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


            val dialog = Dialog(context, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.login_alert_window)
        val body = dialog.findViewById(R.id.termsTextView) as TextView
        body.text = termString
        body.movementMethod = LinkMovementMethod.getInstance()

        val body1 = dialog.findViewById(R.id.privacyTextView) as TextView
        body1.text = privacyString
        body1.movementMethod = LinkMovementMethod.getInstance()

        val yesBtn = dialog.findViewById(R.id.positiveBtn) as Button
        val noBtn = dialog.findViewById(R.id.negativeBtn) as Button
        yesBtn.setOnClickListener {
            val loginScreenActivity = LoginScreen()
            when (flag) {
                1 -> {

                    loginScreenActivity.signInGoogle()
                }
                2 -> {
                    loginScreenActivity.loginWithFacebook()
                }
                else -> {
                    Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()


    }
}