package com.breadcrumbsapp.view.rewards

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.RewardsScreenLayoutBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.retrofit.ApiCalls
import com.breadcrumbsapp.util.SessionHandlerClass
import com.google.gson.Gson
import kotlinx.android.synthetic.main.rewards_screen_layout.*
import kotlinx.android.synthetic.main.trails_screen_layout.trails_screen_back_button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class RewardsScreenActivity : AppCompatActivity() {
    private lateinit var rewardsScreenLayoutBinding: RewardsScreenLayoutBinding
    private lateinit var rewardsListScreenAdapter: RewardsListScreenAdapter
    private var interceptor = intercept()
    private lateinit var rewardsListModel: GetRewardsDataModel
    private lateinit var sessionHandlerClass: SessionHandlerClass
    var answerArrayStr: Array<String> = arrayOf("true","","0","2","[false,false,false,false,false,false,false,true,false,false,false,false]","4","true","[true,false,false,false,false]","0","","[true,true,true,true,false]",
        "true","[true,true,false,true,true,false,false,false,false]","true","Teo","153075","Vincent.teo@gmail.com","28/6/1979")

    val answerTempStr:String="[true,,0,2,[false,false,false,false,false,false,false,true,false,false,false,false],3,true,[true,false,false,false,false],0,,[false,false,false,false,true],true,[false,false,false,false,false,false,false,false,true],true,temp_player_name,,temp@gmail.com,8/04/1995]"


    private val dummyData = "{\n" +
            "  \"status\": true,\n" +
            "  \"message\": [\n" +
            "    {\n" +
            "      \"id\": \"64\",\n" +
            "      \"user_id\": \"198\",\n" +
            "      \"reward_id\": \"3\",\n" +
            "      \"redeem_status\": \"1\",\n" +
            "      \"vendor_staff_id\": null,\n" +
            "      \"scan_date\": \"2020-11-27 12:27:04\",\n" +
            "      \"reward_img\": \"uploads/trails/4/reward1.png\",\n" +
            "      \"rewardtitle\": \"Limited Edition Aardvark Pin\",\n" +
            "      \"enddate\": \"2020-12-27 00:00:00\",\n" +
            "      \"qr\": \"BCXWRS\",\n" +
            "      \"details\": \"Be a true Night Safari explorer! Win this Limited Edition Aardvark Pin by completing an activity along each of our 4 Wild About Twilight walking trails at Night Safari and discovering 1 of the 2 Special Discoveries in Augmented Reality (AR).\",\n" +
            "      \"tnc\": null,\n" +
            "      \"redeem_msg_title\": \"Sample Data Protect Wildlife!\",\n" +
            "      \"redeem_msg_body\": \"Over the past year, we have supported 53 conservation projects in Singapore and the region. By visiting us, you help us protect wildlife too.\",\n" +
            "      \"redeem_msg_img\": null,\n" +
            "      \"rid\": \"3\",\n" +
            "      \"survey_id\": \"45\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"65\",\n" +
            "      \"user_id\": \"198\",\n" +
            "      \"reward_id\": \"3\",\n" +
            "      \"redeem_status\": \"1\",\n" +
            "      \"vendor_staff_id\": null,\n" +
            "      \"scan_date\": \"2020-11-27 12:27:04\",\n" +
            "      \"reward_img\": \"uploads/trails/4/reward1.png\",\n" +
            "      \"rewardtitle\": \"Limited Edition Aardvark Pin\",\n" +
            "      \"enddate\": \"2020-12-27 00:00:00\",\n" +
            "      \"qr\": \"BCXWRS\",\n" +
            "      \"details\": \"Be a true Night Safari explorer! Win this Limited Edition Aardvark Pin by completing an activity along each of our 4 Wild About Twilight walking trails at Night Safari and discovering 1 of the 2 Special Discoveries in Augmented Reality (AR).\",\n" +
            "      \"tnc\": null,\n" +
            "      \"redeem_msg_title\": \"Sample Data Protect Wildlife!\",\n" +
            "      \"redeem_msg_body\": \"Over the past year, we have supported 53 conservation projects in Singapore and the region. By visiting us, you help us protect wildlife too.\",\n" +
            "      \"redeem_msg_img\": null,\n" +
            "      \"rid\": \"3\",\n" +
            "      \"survey_id\": \"45\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"66\",\n" +
            "      \"user_id\": \"198\",\n" +
            "      \"reward_id\": \"3\",\n" +
            "      \"redeem_status\": \"0\",\n" +
            "      \"vendor_staff_id\": null,\n" +
            "      \"scan_date\": \"2020-11-27 12:27:04\",\n" +
            "      \"reward_img\": \"uploads/trails/4/reward1.png\",\n" +
            "      \"rewardtitle\": \"Wild Light Aardvark Pin\",\n" +
            "      \"enddate\": \"2020-12-27 00:00:00\",\n" +
            "      \"qr\": \"BCXWRS\",\n" +
            "      \"details\": \"Be a true Night Safari explorer! Win this Limited Edition Aardvark Pin by completing an activity along each of our 4 Wild About Twilight walking trails at Night Safari and discovering 1 of the 2 Special Discoveries in Augmented Reality (AR).\",\n" +
            "      \"tnc\": null,\n" +
            "      \"redeem_msg_title\": \"You Help Us Protect Wildlife!\",\n" +
            "      \"redeem_msg_body\": \"Over the past year, we have supported 53 conservation projects in Singapore and the region. By visiting us, you help us protect wildlife too.\",\n" +
            "      \"redeem_msg_img\": null,\n" +
            "      \"rid\": \"3\",\n" +
            "      \"survey_id\": \"45\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"67\",\n" +
            "      \"user_id\": \"198\",\n" +
            "      \"reward_id\": \"3\",\n" +
            "      \"redeem_status\": \"0\",\n" +
            "      \"vendor_staff_id\": null,\n" +
            "      \"scan_date\": \"2020-11-27 12:27:04\",\n" +
            "      \"reward_img\": \"uploads/trails/4/reward1.png\",\n" +
            "      \"rewardtitle\": \"Wild Light Aardvark Pin\",\n" +
            "      \"enddate\": \"2020-12-27 00:00:00\",\n" +
            "      \"qr\": \"BCXWRS\",\n" +
            "      \"details\": \"Be a true Night Safari explorer! Win this Limited Edition Aardvark Pin by completing an activity along each of our 4 Wild About Twilight walking trails at Night Safari and discovering 1 of the 2 Special Discoveries in Augmented Reality (AR).\",\n" +
            "      \"tnc\": null,\n" +
            "      \"redeem_msg_title\": \"You Help Us Protect Wildlife!\",\n" +
            "      \"redeem_msg_body\": \"Over the past year, we have supported 53 conservation projects in Singapore and the region. By visiting us, you help us protect wildlife too.\",\n" +
            "      \"redeem_msg_img\": null,\n" +
            "      \"rid\": \"3\",\n" +
            "      \"survey_id\": \"45\"\n" +
            "    }\n" +
            "  ]\n" +
            "}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rewardsScreenLayoutBinding = RewardsScreenLayoutBinding.inflate(layoutInflater)
        setContentView(rewardsScreenLayoutBinding.root)

        sessionHandlerClass = SessionHandlerClass(applicationContext)
        // dummyData()
        getUserRewardsHistory()
        addSurveyAPI()


        setOnclickListeners()

    }

    private fun setOnclickListeners() {
        trails_screen_back_button.setOnClickListener {
            finish()
        }

        rb_active.setOnClickListener {
            val activeRewards = rewardsListModel.message.filter { it.redeem_status == NOT_REDEEM_DATA }
            if (activeRewards.isEmpty())
                showToast("Active")
            else {
                rewards_screen_scrollView.visibility = View.VISIBLE
                no_data_found_textView.visibility = View.GONE
                validateRewardsDataList(activeRewards, true)
            }


        }

        rb_past.setOnClickListener {
            val pastRewards = rewardsListModel.message.filter { it.redeem_status == REDEEM_DATA }
            if (pastRewards.isEmpty()) {
                showToast("Past")
                //dummyData()
            } else {
                rewards_screen_scrollView.visibility = View.VISIBLE
                no_data_found_textView.visibility = View.GONE
                validateRewardsDataList(pastRewards, false)
            }
        }

        rewards_details_screen_info.setOnClickListener {
            showRewardsInfo()
        }
    }

    private fun showRewardsInfo() {
        val dialog = Dialog(this, R.style.FirebaseUI_Transparent).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.about_selfie_challenge)
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            window?.setDimAmount(0.5f)
            window?.attributes!!.windowAnimations = R.style.DialogTheme
        }

        with(dialog) {
            val okBtn = findViewById<TextView>(R.id.okButton)
            val logoIconView = findViewById<ImageView>(R.id.logoIconView)
            val challengeTitle = findViewById<TextView>(R.id.challengeTitle)
            val challengeContent = findViewById<TextView>(R.id.challengeContent)

            logoIconView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.quiz_challenge_rewards
                )
            )
            challengeTitle.text = resources.getString(R.string.reward_quiz_challenge_title)
            challengeContent.text = resources.getString(R.string.reward_challenge_content)

            okBtn.setOnClickListener {
                dismiss()
            }
        }

        dialog.show()
    }

    private fun validateRewardsDataList(
        rewardType: List<GetRewardsDataModel.Message>,
        isItOldData: Boolean
    ) {

        when {
            isItOldData -> {
                rb_active.isChecked = true
                rb_past.isChecked = false
                updateUi(rewardType, R.drawable.reward_banner_active)
            }
            !isItOldData -> {
                rb_active.isChecked = false
                rb_past.isChecked = true
                updateUi(rewardType, R.drawable.reward_banner_past)
            }
            else -> showToast("No Rewards found")
        }
    }

    private fun showToast(type: String) {
        rewardsScreenRecyclerView.adapter = null

        rewards_screen_scrollView.visibility = View.GONE
        no_data_found_textView.visibility = View.VISIBLE
        if (type == "Active") {
            no_data_found_textView.text =
                resources.getString(R.string.rewards_screen_active_no_data_content)
        } else if (type == "Past") {
            no_data_found_textView.text =
                resources.getString(R.string.rewards_screen_past_no_data_content)
        }
      //  Toast.makeText(applicationContext, "No $type Rewards found", Toast.LENGTH_SHORT).show()

    }

    private fun addSurveyAPI() {
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
           // jsonObject.put("reward_id", sessionHandlerClass.getSession("login_id"))
            jsonObject.put("reward_id", sessionHandlerClass.getSession("login_id"))
            jsonObject.put("answers", answerTempStr)

            println("addSurveyAPI Input = $jsonObject")

            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)
                val response = service.addSurvey(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )
                if (response.isSuccessful) {
                    println("addSurveyAPI Successful ${response.body().toString()}")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }
    private fun getUserRewardsHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val userRewardsList = ApiCalls.rewardsService.getUserRewardsList(
                resources.getString(R.string.api_access_token),
                getRequestBody()
            )


            if (userRewardsList.isSuccessful) {
                userRewardsList.body()?.let {
                    withContext(Dispatchers.Main) {
                        rewardsListModel = it
                        println("getRequestBody = ${rewardsListModel.message.size}")
                        //  dummyData()
                        val activeRewards =
                            rewardsListModel.message.filter { it.redeem_status == NOT_REDEEM_DATA }
                        rewards_screen_scrollView.visibility = View.VISIBLE
                        no_data_found_textView.visibility = View.GONE
                        validateRewardsDataList(activeRewards, true)
                        rb_active.performClick()
                    }
                }
            } else
                println("something went wrong, pls try again")
        }
    }

    private fun dummyData() {
        rewardsListModel =
            Gson().fromJson(dummyData, GetRewardsDataModel::class.java)
    }

    private fun updateUi(getRewardsListModel: List<GetRewardsDataModel.Message>, bgColour: Int) {
        rewardsListScreenAdapter =
            RewardsListScreenAdapter(applicationContext, getRewardsListModel, bgColour)
        rewardsScreenRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            adapter = rewardsListScreenAdapter
        }
    }

    private fun getRequestBody(): RequestBody {
        val mediaType = MEDIA_TYPE.toMediaTypeOrNull()
        return JSONObject().apply {
            put("user_id", sessionHandlerClass.getSession("login_id"))
        }.toString().toRequestBody(mediaType)


        println("getRequestBody = ${sessionHandlerClass.getSession("login_id")}")
    }

    companion object {
        private const val USER_ID = "4748"
        private const val REDEEM_DATA = "1"
        private const val NOT_REDEEM_DATA = "0"
        private const val MEDIA_TYPE = "application/json"
    }
}