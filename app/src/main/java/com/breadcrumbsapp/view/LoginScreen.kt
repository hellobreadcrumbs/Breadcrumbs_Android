package com.breadcrumbsapp.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
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
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.breadcrumbsapp.R
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.SessionHandlerClass
import com.breadcrumbsapp.viewmodel.LoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


const val RC_SIGN_IN = 123

class LoginScreen : AppCompatActivity() {
    // Session class
    private lateinit var sharedPreference: SessionHandlerClass
    private var emailID: String = ""
    private var userName: String = ""
    private val registerPlatform: String = "android"
    private var interceptor = intercept()
    private var isFaceBookLogin: Boolean = false
    private var isGooglePlusLogin: Boolean = false
    lateinit var loginViewModel: LoginViewModel
    private lateinit var callbackManager: CallbackManager


    companion object {
         lateinit var mGoogleSignInClient: GoogleSignInClient
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_activity)
        sharedPreference = SessionHandlerClass(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInButton: CardView = findViewById(R.id.googleSignInButton)

        googleSignInButton.setOnClickListener {
            isGooglePlusLogin = true
            isFaceBookLogin = false

            sharedPreference.saveBoolean("isGooglePlusLogin", isGooglePlusLogin)
            sharedPreference.saveBoolean("isFaceBookLogin", isFaceBookLogin)


            //signInGoogle()

            //loginViewModel.alertBeforeSignIn(1,applicationContext)
            alertBeforeSignIn(1)
        }


        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            /* val personName = acct.displayName
             val personGivenName = acct.givenName
             val personFamilyName = acct.familyName
             val personEmail = acct.email
             val personId = acct.id
             val personPhoto: Uri = acct.photoUrl
 */
            updateUI(acct)
        }

        val facebookSignInButton: CardView = findViewById(R.id.facebookSignInButton)
        facebookSignInButton.setOnClickListener {
            isFaceBookLogin = true
            isGooglePlusLogin = true
            sharedPreference.saveBoolean("isGooglePlusLogin", isGooglePlusLogin)
            sharedPreference.saveBoolean("isFaceBookLogin", isFaceBookLogin)

            //loginWithFacebook()

            // loginViewModel.alertBeforeSignIn(2,applicationContext)
            alertBeforeSignIn(2)
        }


        val spannableString = SpannableString(getString(R.string.terms_privacy_content))
        val termsSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#694627")
            }

            override fun onClick(widget: View) {
                widget.setOnClickListener {
                    loginViewModel.webViewForTerms(applicationContext)
                    widget.invalidate()
                }
            }
        }
        val privacySpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#694627")

            }

            override fun onClick(widget: View) {
                widget.setOnClickListener {
                    loginViewModel.webViewForPrivacy(applicationContext)
                    widget.invalidate()
                }
            }
        }

        spannableString.setSpan(termsSpan, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacySpan, 21, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val termsTextView: TextView = findViewById(R.id.termsTextView)
        termsTextView.text = spannableString

        termsTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun alertBeforeSignIn(flag: Int) {

        // T&C Content with Link
        val termString = SpannableString(getString(R.string.alert_view_terms_content))
        val termsSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#79856C")
            }

            override fun onClick(widget: View) {
                widget.setOnClickListener {
                    loginViewModel.webViewForTerms(applicationContext)
                    widget.invalidate()
                }
            }
        }
        termString.setSpan(termsSpan, 31, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        // Privacy Content with link
        val privacyString = SpannableString(getString(R.string.alert_view_privacy_policy))
        val privacySpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = Color.parseColor("#79856C")
            }

            override fun onClick(widget: View) {
                widget.setOnClickListener {
                    loginViewModel.webViewForPrivacy(applicationContext)
                    widget.invalidate()
                }
            }
        }
        privacyString.setSpan(privacySpan, 8, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val dialog = Dialog(this, R.style.FirebaseUI_Transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.login_alert_window)
        val body = dialog.findViewById(R.id.termsTextView) as TextView
        body.text = termString

        body.movementMethod = LinkMovementMethod.getInstance()

        val body1 = dialog.findViewById(R.id.privacyTextView) as TextView
        body1.text = privacyString

        body1.movementMethod = LinkMovementMethod.getInstance()

        val yesBtn = dialog.findViewById(R.id.positiveBtn) as TextView
        val noBtn = dialog.findViewById(R.id.negativeBtn) as Button
        noBtn.paintFlags = noBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        yesBtn.setOnClickListener {

            when (flag) {
                1 -> {
                    signInGoogle()
                }
                2 -> {
                    loginWithFacebook()
                }
                else -> {
                    Toast.makeText(this@LoginScreen, "Please Try Again", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.window!!.attributes!!.windowAnimations = R.style.DialogTheme
        dialog.show()


    }

    fun loginWithFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.let {
                    println("FB Token = ${it.accessToken.token}")
                    val request: GraphRequest = GraphRequest.newMeRequest(
                        result.accessToken
                    ) { _, response ->
                        println("Fb response = $response")
                        if (response.error != null) {
                            // handle error
                            println("Fb response = $response")
                        } else {
                            try {

                                val email = response.jsonObject["email"].toString()
                                val name = response.jsonObject["first_name"].toString()
                                println("***FB Details = $email : $name")
                                sharedPreference.saveSession("player_name", name)
                                if (email != "") {
                                    emailID = email
                                    socialRegister()

                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }

                    val parameters = Bundle()
                    parameters.putString(
                        "fields",
                        "id, first_name, last_name, email,gender"
                    )

                    request.parameters = parameters
                    request.executeAsync()


                }
            }


            override fun onCancel() {
                Toast.makeText(
                    this@LoginScreen,
                    "Facebook login cancelled",
                    Toast.LENGTH_SHORT
                ).show()
                isGooglePlusLogin = false
                isFaceBookLogin = false

                sharedPreference.saveBoolean("isGooglePlusLogin", isGooglePlusLogin)
                sharedPreference.saveBoolean("isFaceBookLogin", isFaceBookLogin)

            }

            override fun onError(error: FacebookException?) {


                Toast.makeText(
                    this@LoginScreen,
                    "Facebook login failed: ${error.toString()}",
                    Toast.LENGTH_SHORT
                ).show()

                isGooglePlusLogin = false
                isFaceBookLogin = false

                sharedPreference.saveBoolean("isGooglePlusLogin", isGooglePlusLogin)
                sharedPreference.saveBoolean("isFaceBookLogin", isFaceBookLogin)
            }

        })
    }

    fun signInGoogle() {


        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //  callbackManager.onActivityResult(requestCode, resultCode, data)
        if (sharedPreference.getBoolean("isFaceBookLogin")) {

            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)

            // Toast.makeText(applicationContext,""+account.displayName,Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            e.printStackTrace()
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //  Log.w(TAG, "signInResult:failed code=" + e.getStatusCode())
            //updateUI(null)
        }
    }


    private fun updateUI(account: GoogleSignInAccount) {
        if (account != null) {
            // NEEDS TO CALL ModelView CLASS & DEFINE FURTHER PROCESS FROM THAT.

            sharedPreference.saveSession("email", account.email.toString())


            emailID = account.email.toString()



            println("******* UpdateUI ${account.photoUrl}")
            sharedPreference.saveSession("player_name", account.displayName.toString())
            if (account.photoUrl != null) {

                sharedPreference.saveSession("player_photo_url", account.photoUrl.toString())
            }
            socialRegister()
        }
    }

    private fun socialRegister() {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.staging_url))
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("email", emailID)
        jsonObject.put("username", userName)
        jsonObject.put("register_platform", registerPlatform)


        println("Register Input = $jsonObject")
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.socialRegister(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to  JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val registerJSON = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    val jsonElement: JsonElement? = JsonParser.parseString(registerJSON)
                    val jsonObject: JsonObject? = jsonElement?.asJsonObject

                    val status: Boolean = jsonObject?.get("status")!!.asBoolean
                    if (!status) {
                        //  Toast.makeText(this@LoginScreenActivity,"Already Registered!",Toast.LENGTH_SHORT).show()
                        socialLogin()
                    } else {

                        println("isLogin From Login ")
                        sharedPreference.saveBoolean("isLogin", true)

                        startActivity(
                            Intent(
                                this@LoginScreen,
                                DiscoverScreenActivity::class.java
                            ).putExtra("isFromLogin", "yes")
                        )
                        overridePendingTransition(
                            R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left
                        )
                        finish()
                    }
                } else {

                    println("Printed JSON ELSE : ${response.code()}")

                }
            }
        }
    }

    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }
    // Social Login

    private fun socialLogin() {


        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()
        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(resources.getString(R.string.staging_url))
            .client(okHttpClient)
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("email", emailID)

        println("Login Input = $jsonObject")
        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.socialLogin(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to  JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val registerJSON = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )
                    val jsonElement: JsonElement? = JsonParser.parseString(registerJSON)
                    val jsonObject: JsonObject? = jsonElement?.asJsonObject

                    val status: Boolean = jsonObject?.get("status")!!.asBoolean
                    println("Login Status = $jsonElement")
                    println("Login ID = ${jsonObject.getAsJsonObject("message").get("id")}")
                    if (status) {


                        val loginString = jsonObject.getAsJsonObject("message").get("id").asInt

                        sharedPreference.saveSession("login_details", jsonElement.toString())
                        sharedPreference.saveSession("login_id", loginString.toString())
                        sharedPreference.saveBoolean("isLogin", true)



                        Toast.makeText(
                            this@LoginScreen,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        getUserDetails()

                        /* sharedPreference.saveBoolean("isLogin", true)

                     startActivity(
                         Intent(
                             this@LoginScreen,
                             DiscoverScreenActivity::class.java
                         ).putExtra("isFromLogin","yes")
                     )
                     overridePendingTransition(
                         R.anim.anim_slide_in_left,
                         R.anim.anim_slide_out_left
                     )
                     finish()*/
                    }
                } else {

                    println("Printed JSON ELSE : ${response.code()}")

                }
            }
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // ...
            }
    }


    private fun getUserDetails() {

        try {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build()


            // Create Retrofit

            val retrofit = Retrofit.Builder()
                .baseUrl(resources.getString(R.string.staging_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create JSON using JSONObject

            val jsonObject = JSONObject()
            jsonObject.put("user_id", sharedPreference.getSession("login_id"))

            println("getUserDetails Url = ${resources.getString(R.string.staging_url)}")
            println("getUserDetails Input = $jsonObject")


            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)



            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.getUserDetails(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )




                if (response.isSuccessful) {
                    if (response.body()!!.status) {
                        if (response.body()!!.message != null) {

                            CommonData.getUserDetails = response.body()?.message




                            println("GetUseDetails = ${CommonData.getUserDetails!!.username}")

                            sharedPreference.saveSession("player_experience_points",CommonData.getUserDetails!!.experience)
                            sharedPreference.saveSession("player_register_date",CommonData.getUserDetails!!.created)
                            sharedPreference.saveSession("player_user_name",CommonData.getUserDetails!!.username)
                            sharedPreference.saveSession("player_email_id",CommonData.getUserDetails!!.email)
                            sharedPreference.saveSession("player_id",CommonData.getUserDetails!!.id)




                            startActivity(
                                Intent(
                                    this@LoginScreen,
                                    DiscoverScreenActivity::class.java
                                ).putExtra("isFromLogin","yes")
                            )
                            overridePendingTransition(
                                R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left
                            )
                            finish()
                        } else {

                        }
                    }
                }


            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        //moveTaskToBack(true)
    }
}


/*


LOGIN API RESPONSE...
{
    "status": true,
    "message": {
    "id": "4700",
    "email": "cakshine@gmail.com",
    "profile_picture": "",
    "username": "Arun Kumar",
    "password": "",
    "rank": "1",
    "finished_pois": "0",
    "finished_trails": "0",
    "experience": "250",
    "is_sponsor": "0",
    "register_platform": "1",
    "created": "2021-04-19 03:21:37",
    "updated": "2021-04-19 03:21:37",
    "token": "token value removed from here for security"
}
}*/
