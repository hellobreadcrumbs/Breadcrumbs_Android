package com.breadcrumbsapp.view.rewards

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.RewardsScreenLayoutBinding
import com.breadcrumbsapp.retrofit.ApiCalls
import com.google.gson.Gson
import kotlinx.android.synthetic.main.rewards_screen_layout.*
import kotlinx.android.synthetic.main.trails_screen_layout.trails_screen_back_button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class RewardsScreenActivity : AppCompatActivity() {
    private lateinit var rewardsScreenLayoutBinding: RewardsScreenLayoutBinding
    private lateinit var rewardsListScreenAdapter: RewardsListScreenAdapter

    private lateinit var rewardsListModel: GetRewardsDataModel

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

        dummyData()
       // getUserRewardsHistory()

        val activeRewards = rewardsListModel.message.filter { it.redeem_status == ACTIVE_DATA }
        if (activeRewards.isEmpty())
            showToast("Active")
        else
        {
            rewards_screen_scrollView.visibility=View.VISIBLE
            no_data_found_textView.visibility= View.GONE
            validateRewardsDataList(activeRewards, true)
        }

        setOnclickListeners()
    }

    private fun setOnclickListeners() {
        trails_screen_back_button.setOnClickListener {
            finish()
        }

        rb_active.setOnClickListener {
            val activeRewards = rewardsListModel.message.filter { it.redeem_status == ACTIVE_DATA }
            if (activeRewards.isEmpty())
                showToast("Active")
            else
            {
                rewards_screen_scrollView.visibility=View.VISIBLE
                no_data_found_textView.visibility= View.GONE
                validateRewardsDataList(activeRewards, true)
            }


        }

        rb_past.setOnClickListener {
            val pastRewards = rewardsListModel.message.filter { it.redeem_status == PAST_DATA }
            if (pastRewards.isEmpty())
            {
                showToast("Past")
                //dummyData()
            }


            else
            {
                rewards_screen_scrollView.visibility=View.VISIBLE
                no_data_found_textView.visibility= View.GONE
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
        rewardsScreenRecyclerView.adapter=null

        rewards_screen_scrollView.visibility=View.GONE
        no_data_found_textView.visibility= View.VISIBLE
        if(type=="Active")
        {
            no_data_found_textView.text=resources.getString(R.string.rewards_screen_active_no_data_content)
        }
        else if (type=="Past")
        {
            no_data_found_textView.text=resources.getString(R.string.rewards_screen_past_no_data_content)
        }
        Toast.makeText(applicationContext, "No $type Rewards found", Toast.LENGTH_SHORT)
            //.show()
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

                      //  dummyData()
                        val activeRewards =
                            rewardsListModel.message.filter { it.redeem_status == ACTIVE_DATA }
                        rewards_screen_scrollView.visibility=View.VISIBLE
                        no_data_found_textView.visibility= View.GONE
                        validateRewardsDataList( activeRewards, true)
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
            put("user_id", USER_ID)
        }.toString().toRequestBody(mediaType)
    }

    companion object {
        private const val USER_ID = "198"
        private const val PAST_DATA = "0"
        private const val ACTIVE_DATA = "1"
        private const val MEDIA_TYPE = "application/json"
    }
}