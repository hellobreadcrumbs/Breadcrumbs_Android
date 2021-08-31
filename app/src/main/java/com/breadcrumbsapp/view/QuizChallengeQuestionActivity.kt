package com.breadcrumbsapp.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.QuizChallengeQuestionActivityBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData.Companion.eventsModelMessage
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.google.gson.*
import kotlinx.android.synthetic.main.challenge_activity.*
import kotlinx.android.synthetic.main.quiz_challenge.*
import kotlinx.android.synthetic.main.quiz_challenge_question_activity.*
import kotlinx.android.synthetic.main.selfie_challenge_level_layout.*
import kotlinx.android.synthetic.main.user_profile_screen_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit


/*

ch_type =0,1,2
ch_question = ? , null , Single Question , array values

ch_type =0 and ch_question=?  => No Question & Answer
ch_type =0 and ch_question=Single Question  => Single Question & Answer

ch_type =1 and ch_question=Single Question  => Single Question & Answer

ch_type =2 and ch_question=array values  => Multiple Question & Answer
 */


class QuizChallengeQuestionActivity : AppCompatActivity() {

    private lateinit var binding: QuizChallengeQuestionActivityBinding
    private var selectedPOIID: String = ""

    private var isClicked: Boolean = false
    private var clickedPos: Int = -1
    private var submitButtonClickingCount: Int = 0
    private lateinit var questionObj: JsonArray
    private lateinit var answerObj: JsonArray
    private lateinit var finalAnswerObj: JsonArray
    private var singleQuestion: String = ""
    private lateinit var questionType: String
    private var finalAnswer: String = ""
    private var chSetAnswers: String = ""
    private var continueBtn = 0
    private var completeImagePath = ""
    private var scoredValue = 0
    private var overallValue = 12000
    private var quiz_answer_value = 0
    private var discover_value = 1000
    private var poiImage: String = ""
    private var interceptor = intercept()
    private lateinit var sharedPreference: SessionHandlerClass

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizChallengeQuestionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SessionHandlerClass(applicationContext)
        selectedPOIID = sharedPreference.getSession("selectedPOIID").toString()


        val bundle: Bundle = intent.extras!!
        poiImage = bundle.getString("poiImage")!!

        println("poiImage = $poiImage")

        setQuestion()
        setAnswer()

        quizChallenge_backButton.setOnClickListener {
            finish()
        }
        answerOneLayout.setOnClickListener {

            isClicked = true
            clickedPos = 0

            answerOneLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg_yellow)
            answerTwoLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg)
            answerThreeLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg)

            submitButton.background = getDrawable(R.drawable.submit_active)

            submitClick()
        }
        answerTwoLayout.setOnClickListener {
            isClicked = true
            clickedPos = 1

            answerOneLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg)
            answerTwoLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg_yellow)
            answerThreeLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg)

            submitButton.background = getDrawable(R.drawable.submit_active)
            submitClick()
        }
        answerThreeLayout.setOnClickListener {

            isClicked = true
            clickedPos = 2

            answerOneLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg)
            answerTwoLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg)
            answerThreeLayout.background = getDrawable(R.drawable.quiz_challenge_answer_bg_yellow)

            submitButton.background = getDrawable(R.drawable.submit_active)
            //    answerThreeLayout.alpha=0.5f
            submitClick()
        }


    }


    private fun setQuestion() {
        for (i in eventsModelMessage!!.indices) {
            if (eventsModelMessage!![i].id == selectedPOIID) {


                questionType = eventsModelMessage!![i].ch_type
                val chSelection = eventsModelMessage!![i].ch_question

                println("Set of question Type= $questionType")
                println("Set of question = $chSelection")

                try {
//eventsModelMessage!![i].ch_image != "1" || eventsModelMessage!![i].ch_image != "0" ||
                    println("Image = ${eventsModelMessage!![i].ch_image}")
                    if (eventsModelMessage!![i].ch_image != null) {
                        println("Image = IF 1 : ${eventsModelMessage!![i].ch_image}")
                        if (eventsModelMessage!![i].ch_image != "1" || eventsModelMessage!![i].ch_image != "0") {
                            println("Image = IF 2 : ${eventsModelMessage!![i].ch_image}")
                            var image =
                                eventsModelMessage!![i].ch_image.replace(
                                    "<img width='100%' src='",
                                    ""
                                )
                            completeImagePath = image.replace("' />", "")
                            println("QuestionImage: $completeImagePath")


                            if (completeImagePath.contains("https") || completeImagePath.contains("http")) {
                                Glide.with(applicationContext).load(completeImagePath)
                                    .into(questionImage)
                            } else {
                                Glide.with(applicationContext).load(poiImage).into(questionImage)
                            }


                        } else {
                            println("Image = ELSE : ${eventsModelMessage!![i].ch_image}")
                            Glide.with(applicationContext)
                                .load(resources.getDrawable(R.drawable.poi_photo))
                                .into(questionImage)
                        }


                    } else {
                        Glide.with(applicationContext)
                            .load(poiImage)
                            .into(questionImage)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }



                when {
                    questionType.toInt() == 1 -> {
                        singleQuestion = eventsModelMessage!![i].ch_question
                        submitButtonClickingCount = 1
                        questionText.text = singleQuestion

                    }
                    questionType.toInt() == 2 -> {
                        try {
                            questionObj = JsonParser.parseString(chSelection) as JsonArray

                            submitButtonClickingCount = 1
                            questionTitle.text =
                                "QUESTION $submitButtonClickingCount/" + "${questionObj.size()}"
                            questionText.text = questionObj[0].toString().replace("\"", "")


                        } catch (e: Exception) {
                            e.printStackTrace()


                        }
                    }
                    questionType.toInt() == 0 -> {

                    }
                }


                /*
                 DRAFT COPY...

                 try {
                     questionObj = JsonParser.parseString(chSelection) as JsonArray

                     submitButtonClickingCount = 1
                     questionTitle.text =
                         "QUESTION $submitButtonClickingCount/" + "${questionObj.size()}"
                     questionText.text = questionObj[0].toString().replace("\"", "")




                 } catch (e: Exception) {
                     e.printStackTrace()

                     singleQuestion=eventsModelMessage!![i].ch_question

                     submitButtonClickingCount = 1
                 }*/

            }

        }
    }

    private fun setAnswer() {
        for (i in eventsModelMessage!!.indices) {
            if (eventsModelMessage!![i].id == selectedPOIID) {

                val chSelection = eventsModelMessage!![i].ch_selections
                finalAnswer = eventsModelMessage!![i].ch_answer
                chSetAnswers = eventsModelMessage!![i].ch_set_answer




                println("Set of chSelection = $chSelection")
                println("Set of finalAnswer = $finalAnswer")

                when {
                    questionType.toInt() == 1 -> {

                        answerObj = JsonParser.parseString(chSelection) as JsonArray
                        println("ANS ELSE: ${answerObj[0]}")
                        answerOne.text = answerObj[0].toString().replace("\"", "")
                        answerTwo.text = answerObj[1].toString().replace("\"", "")
                        answerThree.text = answerObj[2].toString().replace("\"", "")



                        println("finalAnswer = $finalAnswer")

                    }
                    questionType.toInt() == 2 -> {
                        try {

                            answerObj = JsonParser.parseString(chSelection) as JsonArray

                            val subAnswerObj =
                                JsonParser.parseString(answerObj[0].toString()) as JsonArray
                            answerOne.text = subAnswerObj[0].toString().replace("\"", "")
                            answerTwo.text = subAnswerObj[1].toString().replace("\"", "")
                            answerThree.text = subAnswerObj[2].toString().replace("\"", "")


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    questionType.toInt() == 0 -> {

                    }
                }


                /*try {
                    println("ANS : ${eventsModelMessage!![i].ch_set_answer}")
                    if (eventsModelMessage!![i].ch_set_answer ==null)
                    {
                    answerObj = JsonParser.parseString(chSelection) as JsonArray
                    val subAnswerObj = JsonParser.parseString(answerObj[0].toString()) as JsonArray
                    answerOne.text = subAnswerObj[0].toString().replace("\"", "")
                    answerTwo.text = subAnswerObj[1].toString().replace("\"", "")
                    answerThree.text = subAnswerObj[2].toString().replace("\"", "")

                    println("Set Answer = ${eventsModelMessage!![i].ch_answer} , ${eventsModelMessage!![i].ch_set_answer}")



                          subSetAnswerObj=JsonParser.parseString(eventsModelMessage!![i].ch_set_answer) as JsonArray


                        for( k in 0 until subSetAnswerObj.count())
                        {
                            println("subSetAnswerObj = ${subSetAnswerObj[k]}")
                        }
                    }
                    else{
                        answerObj = JsonParser.parseString(chSelection) as JsonArray
                        println("ANS ELSE: ${answerObj[0]}")
                        answerOne.text = answerObj[0].toString().replace("\"", "")
                        answerTwo.text = answerObj[1].toString().replace("\"", "")
                        answerThree.text = answerObj[2].toString().replace("\"", "")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }*/
            }

        }

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun submitClick() {
        submitButton.setOnClickListener {
            if (isClicked && clickedPos >= 0) {


                when {
                    questionType.toInt() == 1 -> {

                        if (submitButton.text == "CONTINUE") {
                            questionLayout.visibility = View.GONE
                            quizChallengeLevelLayout.visibility = View.VISIBLE
                            quizChallenge_backButton.visibility = View.INVISIBLE

                            poiTitle.text = sharedPreference.getSession("selectedPOIName")
                            // Glide.with(applicationContext).load(completeImagePath).into(challengeLevelImage)

                            if (completeImagePath.contains("https")) {
                                Glide.with(applicationContext).load(completeImagePath)
                                    .into(challengeLevelImage)
                            } else {
                                Glide.with(applicationContext).load(poiImage)
                                    .into(challengeLevelImage)
                            }


                            calculateXPPoints()


                            quizChallengeCloseButton.setOnClickListener {
                                /*  startActivity(
                                      Intent(
                                          this@QuizChallengeQuestionActivity,
                                          DiscoverScreenActivity::class.java
                                      ).putExtra("isFromLogin", "no")
                                  )
                                  overridePendingTransition(
                                      R.anim.anim_slide_in_left,
                                      R.anim.anim_slide_out_left
                                  )
                                  finish()*/
                                discoverPOI()
                            }
                        } else {
                            submitButton.text = "CONTINUE"
                            submitButton.background = getDrawable(R.drawable.selfie_continue_btn)
                            quizChallenge_backButton.visibility = View.INVISIBLE


                            if (clickedPos == finalAnswer.toInt()) {
                                println("Result : $clickedPos , $finalAnswer")
                                quiz_answer_value = 50
                                when (clickedPos) {
                                    0 -> {
                                        answerOneLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)

                                    }
                                    1 -> {
                                        answerTwoLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    }
                                    2 -> {
                                        answerThreeLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    }
                                }

                            } else {
                                quiz_answer_value = 0
                                println("Result = $clickedPos , $finalAnswer")
                                when (clickedPos) {
                                    0 -> {
                                        answerOneLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_red)
                                        answerOneLayout.alpha = 0.5f
                                    }
                                    1 -> {
                                        answerTwoLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_red)
                                        answerTwoLayout.alpha = 0.5f
                                    }
                                    2 -> {
                                        answerThreeLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_red)
                                        answerThreeLayout.alpha = 0.5f
                                    }
                                }

                                when (finalAnswer.toInt()) {
                                    0 -> {
                                        answerOneLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    }
                                    1 -> {
                                        answerTwoLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    }
                                    2 -> {
                                        answerThreeLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    }
                                }
                            }
                        }

                    }

                    questionType.toInt() == 2 -> {

                        // try {
                        println("Btn Click - questionType = $questionType")

                        try {
                            finalAnswerObj = JsonParser.parseString(chSetAnswers) as JsonArray
                            println("Btn Click - finalAnswerObj = $finalAnswerObj")

                            val finalAnswerArrayObj =
                                JsonParser.parseString(finalAnswerObj.toString()) as JsonArray

                            println("finalAnswerArrayObj ${finalAnswerArrayObj.size()}")
                            for (kk in 0 until finalAnswerArrayObj.size()) {
                                println("finalAnswerArrayObj Details : ${finalAnswerArrayObj[kk]}")

                                if (finalAnswerArrayObj[kk].toString() == "0") {
                                    println("finalAnswerArrayObj Details IF : ${finalAnswerArrayObj[kk]}")
                                    val ss: String = finalAnswerArrayObj[kk].toString()
                                    println("finalAnswerArrayObj Details IF ss : $ss")
                                }

                            }

                            if (submitButton.text == "CONTINUE") {
                                quizChallenge_backButton.visibility = View.INVISIBLE

                                continueBtn++

                                println("Submit Btn Ctnue : $continueBtn")
                                if (continueBtn == 3) {
                                    submitButton.text = "CONTINUE"
                                    submitButton.background =
                                        getDrawable(R.drawable.selfie_continue_btn)

                                    println("Submit Btn Ctnue IF : $continueBtn")

                                    questionLayout.visibility = View.GONE
                                    quizChallengeLevelLayout.visibility = View.VISIBLE
                                    poiTitle.text = sharedPreference.getSession("selectedPOIName")
                                    //Glide.with(applicationContext).load(completeImagePath).into(challengeLevelImage)

                                    if (completeImagePath.contains("https")) {
                                        Glide.with(applicationContext).load(completeImagePath)
                                            .into(challengeLevelImage)
                                    } else {
                                        Glide.with(applicationContext).load(poiImage)
                                            .into(challengeLevelImage)
                                    }
                                    calculateXPPoints()


                                    quizChallengeCloseButton.setOnClickListener(View.OnClickListener {
                                        /* startActivity(
                                             Intent(
                                                 this@QuizChallengeQuestionActivity,
                                                 DiscoverScreenActivity::class.java
                                             ).putExtra("isFromLogin", "no")
                                         )
                                         overridePendingTransition(
                                             R.anim.anim_slide_in_left,
                                             R.anim.anim_slide_out_left
                                         )
                                         finish()*/
                                        discoverPOI()
                                    })

                                } else {
                                    quizChallenge_backButton.visibility = View.INVISIBLE
                                    submitButton.text = "SUBMIT"
                                    submitButton.background = getDrawable(R.drawable.submit_active)

                                    isClicked = false
                                    clickedPos = 0

                                    answerOneLayout.background =
                                        getDrawable(R.drawable.quiz_challenge_answer_bg)
                                    answerTwoLayout.background =
                                        getDrawable(R.drawable.quiz_challenge_answer_bg)
                                    answerThreeLayout.background =
                                        getDrawable(R.drawable.quiz_challenge_answer_bg)

                                    answerOneLayout.alpha = 1.0f
                                    answerTwoLayout.alpha = 1.0f
                                    answerThreeLayout.alpha = 1.0f

                                    println("Continue Btn = $submitButtonClickingCount , ${questionObj.size()}")
                                    if (submitButtonClickingCount <= questionObj.size()) {
                                        questionTitle.text =
                                            "QUESTION $submitButtonClickingCount/" + "${questionObj.size()}"
                                        questionText.text =
                                            questionObj[submitButtonClickingCount - 1].toString()
                                                .replace("\"", "")

                                        val subAnswerObj =
                                            JsonParser.parseString(answerObj[submitButtonClickingCount - 1].toString()) as JsonArray
                                        answerOne.text =
                                            subAnswerObj[0].toString().replace("\"", "")
                                        answerTwo.text =
                                            subAnswerObj[1].toString().replace("\"", "")
                                        answerThree.text =
                                            subAnswerObj[2].toString().replace("\"", "")
                                    }
                                }

                            } else if (submitButton.text == "SUBMIT") {
                                quizChallenge_backButton.visibility = View.INVISIBLE
                                if (clickedPos == finalAnswerObj[submitButtonClickingCount - 1].asInt) {
                                    println("Clicked Pos IF = $clickedPos , ${finalAnswerObj[submitButtonClickingCount - 1]}")
                                    if (finalAnswerObj[submitButtonClickingCount - 1].toString() == "0") {
                                        println("Clicked Pos IF = Ama da ${finalAnswerObj[submitButtonClickingCount - 1]}")

                                        answerOneLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    } else {
                                        when (clickedPos) {
                                            0 -> {
                                                answerOneLayout.background =
                                                    getDrawable(R.drawable.quiz_challenge_answer_bg_green)

                                            }
                                            1 -> {
                                                answerTwoLayout.background =
                                                    getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                            }
                                            2 -> {
                                                answerThreeLayout.background =
                                                    getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                            }
                                        }
                                    }


                                } else {
                                    quizChallenge_backButton.visibility = View.INVISIBLE
                                    println("Clicked Pos ELSE = $clickedPos , ${finalAnswerObj[submitButtonClickingCount - 1]}")
                                    if (finalAnswerObj[submitButtonClickingCount - 1].toString() == "0") {
                                        println("Clicked Pos ELSE = Ama da ${finalAnswerObj[submitButtonClickingCount - 1]}")

                                        answerOneLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    } else {
                                        when (finalAnswerObj[submitButtonClickingCount - 1].asInt) {
                                            0 -> {
                                                answerOneLayout.background =
                                                    getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                            }
                                            1 -> {
                                                answerTwoLayout.background =
                                                    getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                            }
                                            2 -> {
                                                answerThreeLayout.background =
                                                    getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                            }
                                        }
                                    }
                                    when (clickedPos) {
                                        0 -> {
                                            answerOneLayout.background =
                                                getDrawable(R.drawable.quiz_challenge_answer_bg_red)
                                            answerOneLayout.alpha = 0.5f
                                        }
                                        1 -> {
                                            answerTwoLayout.background =
                                                getDrawable(R.drawable.quiz_challenge_answer_bg_red)
                                            answerTwoLayout.alpha = 0.5f
                                        }
                                        2 -> {
                                            answerThreeLayout.background =
                                                getDrawable(R.drawable.quiz_challenge_answer_bg_red)
                                            answerThreeLayout.alpha = 0.5f
                                        }
                                    }


                                }
                                submitButton.text = "CONTINUE"
                                submitButton.background =
                                    getDrawable(R.drawable.selfie_continue_btn)
                                submitButtonClickingCount += 1

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                    questionType.toInt() == 0 -> {

                    }
                }


            } else {
                //submitButton.text = "CLOSE"
            }


        }
    }


    override fun onBackPressed() {
        // super.onBackPressed()
        Toast.makeText(applicationContext, "Please click CLOSE button", Toast.LENGTH_SHORT).show()
    }

    private fun intercept(): HttpLoggingInterceptor {
        val interceptors = HttpLoggingInterceptor()
        interceptors.level = HttpLoggingInterceptor.Level.BODY
        interceptor = interceptors
        return interceptor
    }


    private fun calculateXPPoints() {
/*

  With Default Data..
 determinateBar.max = overallValue
        discover_value = CommonData.getUserDetails!!.experience.toInt()
        scoredValue = discover_value + quiz_answer_value
        quiz_answer.text = "+$quiz_answer_value XP"

        var totalScore = 0
        for (i in 0 until CommonData.eventsModelMessage!!.count()) {
            if (eventsModelMessage!![i].disc_id != null) {
                totalScore += eventsModelMessage!![i].experience.toInt()
                println("totalScore :: $totalScore")
            }
        }
        println("totalScore :: $totalScore")
        ObjectAnimator.ofInt(determinateBar, "progress", totalScore)
            .setDuration(1000)
            .start()
        totalScore += scoredValue
        val subtractValue = overallValue - scoredValue
        quizBalanceValue.text = "$subtractValue XP to Level 2"*/


        // Updated One with API data..

        var progressBarMaxValue = sharedPreference.getIntegerSession("xp_point_nextLevel_value")
        var expToLevel = sharedPreference.getIntegerSession("expTo_level_value")
        var completedPoints = sharedPreference.getSession("player_experience_points")
        val levelValue = sharedPreference.getSession("lv_value")
        val presentLevel = sharedPreference.getSession("current_level")
        scoredValue = discover_value + quiz_answer_value
        quiz_answer.text = "+$quiz_answer_value XP"
        determinateBar.max = progressBarMaxValue
        quizBalanceValue.text = "$expToLevel XP TO $levelValue"
        quiz_challenge_level_name.text=presentLevel
        ObjectAnimator.ofInt(determinateBar, "progress", completedPoints!!.toInt())
            .setDuration(1000)
            .start()
    }

    private fun discoverPOI() {


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
                .build()

            // Create Service
            val service = retrofit.create(APIService::class.java)

            // Create JSON using JSONObject
            val jsonObject = JSONObject()
            jsonObject.put("user_id", sharedPreference.getSession("login_id"))
            jsonObject.put("poi_id", sharedPreference.getSession("selectedPOIID"))

            println("Discover_POI Input = $jsonObject")
            // Convert JSONObject to String
            val jsonObjectString = jsonObject.toString()

            // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

            CoroutineScope(Dispatchers.IO).launch {
                // Do the POST request and get response
                val response = service.discoverPOI(requestBody)

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
                        println("Discover_POI Status = $jsonElement")

                        if (status) {


                            startActivity(
                                Intent(
                                    this@QuizChallengeQuestionActivity,
                                    DiscoverScreenActivity::class.java
                                ).putExtra("isFromLogin", "no")
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
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}