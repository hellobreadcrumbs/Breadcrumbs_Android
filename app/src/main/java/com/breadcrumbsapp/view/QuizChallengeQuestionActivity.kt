package com.breadcrumbsapp.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.QuizChallengeQuestionActivityBinding
import com.breadcrumbsapp.interfaces.APIService
import com.breadcrumbsapp.util.CommonData
import com.breadcrumbsapp.util.CommonData.Companion.eventsModelMessage
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.*
import kotlinx.android.synthetic.main.challenge_activity.*
import kotlinx.android.synthetic.main.quiz_challenge.*
import kotlinx.android.synthetic.main.quiz_challenge_question_activity.*
import kotlinx.android.synthetic.main.selfie_challenge_level_layout.*
import kotlinx.android.synthetic.main.trail_details_layout.*
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
import retrofit2.converter.gson.GsonConverterFactory
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
    private var selectedTrailID: String = ""

    private var isClicked: Boolean = false
    private var clickedPos: Int = -1
    private var submitButtonClickingCount: Int = 0
    private lateinit var questionObj: JsonArray
    private lateinit var answerObj: JsonArray
    private lateinit var clickedAnswerArrayList: ArrayList<String>
    private lateinit var finalAnswerObj: JsonArray
    private var singleQuestion: String = ""
    private lateinit var questionType: String
    private var extraExp: String = "50"
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
    private var trailIcons = intArrayOf(
        R.drawable.breadcrumbs_trail,
        R.drawable.wild_about_twlight_icon,
        R.drawable.anthology_trail_icon

    )

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuizChallengeQuestionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SessionHandlerClass(applicationContext)
        selectedPOIID = sharedPreference.getSession("selectedPOIID").toString()
        selectedTrailID = sharedPreference.getSession("selected_trail_id").toString()


        clickedAnswerArrayList = arrayListOf<String>()

        for (i in CommonData.getTrailsData!!.indices) {
            if (CommonData.getTrailsData!![i].id == selectedTrailID) {
                println("Details IF ::: Trail ID = ${CommonData.getTrailsData!![i].id} Completed_POI == ${CommonData.getTrailsData!![i].completed_poi_count}")

                val updatedPoiCount = CommonData.getTrailsData!![i].completed_poi_count.toInt() + 1
                println("updatedPoiCount = $updatedPoiCount")
                quiz_challenge_screen_poi_completed_details.text = "$updatedPoiCount /" +
                        " ${CommonData.getTrailsData!![i].poi_count} POIs DISCOVERED"
            }
        }


        if (selectedTrailID == "4") {
            Glide.with(applicationContext).load(trailIcons[1])
                .into(quiz_challenge_screen_trail_icon)
        } else if (selectedTrailID == "6") {
            Glide.with(applicationContext).load(trailIcons[2])
                .into(quiz_challenge_screen_trail_icon)
        }


        quiz_challenge_screen_discovery_value.text =
            "+${sharedPreference.getSession("selectedPOIDiscovery_XP_Value")} XP"


        val bundle: Bundle = intent.extras!!
        poiImage = bundle.getString("poiImage")!!

        println("poiImage = $poiImage")

        setQuestion()
        setAnswer()

        quizChallenge_backButton.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this@QuizChallengeQuestionActivity,
                    ChallengeActivity::class.java
                )
            )
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

                //   extraExp=eventsModelMessage!![i].uc_extra_exp
                extraExp = eventsModelMessage!![i].ch_experience
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
                                /*   Glide.with(applicationContext).load(completeImagePath)
                                       .into(questionImage)*/

                                try {

                                    Glide.with(applicationContext)
                                        .load(completeImagePath)
                                        .listener(object : RequestListener<Drawable?> {
                                            override fun onLoadFailed(
                                                e: GlideException?,
                                                model: Any?,
                                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                quiz_challenge_screen_loader.visibility = View.GONE
                                                return false
                                            }

                                            override fun onResourceReady(
                                                resource: Drawable?,
                                                model: Any?,
                                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                                dataSource: DataSource?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                quiz_challenge_screen_loader.visibility = View.GONE
                                                return false
                                            }
                                        })
                                        .into(questionImage)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                //     Glide.with(applicationContext).load(poiImage).into(questionImage)

                                try {
                                    Glide.with(applicationContext)
                                        .load(poiImage)
                                        .listener(object : RequestListener<Drawable?> {
                                            override fun onLoadFailed(
                                                e: GlideException?,
                                                model: Any?,
                                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                quiz_challenge_screen_loader.visibility = View.GONE
                                                return false
                                            }

                                            override fun onResourceReady(
                                                resource: Drawable?,
                                                model: Any?,
                                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                                dataSource: DataSource?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                quiz_challenge_screen_loader.visibility = View.GONE
                                                return false
                                            }
                                        })
                                        .into(questionImage)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }


                        } else {
                            println("Image = ELSE : ${eventsModelMessage!![i].ch_image}")



                            try {
                                Glide.with(applicationContext)
                                    .load(completeImagePath)
                                    .listener(object : RequestListener<Drawable?> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            quiz_challenge_screen_loader.visibility = View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            quiz_challenge_screen_loader.visibility = View.GONE
                                            return false
                                        }
                                    })
                                    .into(questionImage)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                    } else {
                        println("_______________________________ ELSE")
                        /*    Glide.with(applicationContext)
                                .load(poiImage)
                                .into(questionImage)*/


                        try {
                            Glide.with(applicationContext)
                                .load(poiImage)
                                .listener(object : RequestListener<Drawable?> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        quiz_challenge_screen_loader.visibility = View.GONE
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        quiz_challenge_screen_loader.visibility = View.GONE
                                        return false
                                    }
                                })
                                .into(questionImage)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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

                            println("Question Screen ::: ${questionObj.size()}")

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


                            // calculateXPPoints()
                            getUserDetails()

                            quizChallengeCloseButton.setOnClickListener {
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
                                //  discoverPOI()
                            }
                        } else {
                            submitButton.text = "CONTINUE"
                            submitButton.background = getDrawable(R.drawable.selfie_continue_btn)
                            quizChallenge_backButton.visibility = View.INVISIBLE


                            if (clickedPos == finalAnswer.toInt()) {
                                println("Result : $clickedPos , $finalAnswer")
                                //   quiz_answer_value = 50

                                quiz_answer_value =
                                    sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!
                                        .toInt()

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
                                // 3 will become no.of.questions...
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
                                    //beginChallengeAPI(clickedAnswerArrayList.toString(),extraExp)
                                    //calculateXPPoints()
                                    getUserDetails()

                                    quizChallengeCloseButton.setOnClickListener(View.OnClickListener {
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
                                        //discoverPOI()
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
                                    println("Multiple Questions Clicked Pos IF :: $clickedPos")
                                    println("Clicked Pos IF 1= $clickedPos , ${finalAnswerObj[submitButtonClickingCount - 1]}")
                                    if (finalAnswerObj[submitButtonClickingCount - 1].toString() == "0") {
                                        println("Clicked Pos IF 2= ${finalAnswerObj[submitButtonClickingCount - 1]}")

                                        answerOneLayout.background =
                                            getDrawable(R.drawable.quiz_challenge_answer_bg_green)
                                    } else {
                                        println("Multiple Questions Clicked Pos ELSE :: $clickedPos")
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
                                        println("Clicked Pos ELSE =${finalAnswerObj[submitButtonClickingCount - 1]}")

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
                                println("Multiple Questions Clicked Pos :: $clickedPos")
                                clickedAnswerArrayList.add(clickedPos.toString())
                                println("Multiple Questions Clicked Pos :: clickedAnswerArrayList = ${clickedAnswerArrayList.size}")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        // beginChallengeAPI(finalAnswer,extraExp)

                    }
                    questionType.toInt() == 0 -> {

                    }
                }


            } else {
                //submitButton.text = "CLOSE"
            }


        }
    }

    private fun beginChallengeAPI(answer: String, extraExp: String) {
        try {

            println("beginChallengeAPI Quiz Input = Before = $answer , $extraExp")
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
            jsonObject.put("poi_id", sharedPreference.getSession("selectedPOIID"))
            jsonObject.put("set_answers", answer)
            jsonObject.put("extra_exp", extraExp)

            println("begin_set_challenge Input = $jsonObject")
            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = jsonObject.toString().toRequestBody(mediaType)
            CoroutineScope(Dispatchers.IO).launch {

                // Create Service
                val service = retrofit.create(APIService::class.java)

                val response = service.beginSetChallenge(
                    resources.getString(R.string.api_access_token),
                    requestBody
                )

                runOnUiThread {


                    discoverPOI()
                }


            }

        } catch (e: Exception) {
            e.printStackTrace()
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

        // Updated on 09-09-2021


        var progressBarMaxValue = sharedPreference.getIntegerSession("xp_point_nextLevel_value")
        val expToLevel = sharedPreference.getIntegerSession("expTo_level_value")
        val completedPoints = sharedPreference.getSession("player_experience_points")
        var levelValue = sharedPreference.getSession("lv_value")
        val presentLevel = sharedPreference.getSession("current_level")
        val poiDiscoverXP: Int =
            sharedPreference.getSession("selectedPOIDiscovery_XP_Value")!!.toInt()
        var poiChallengeXP: Int =
            sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()
        val completedPointIntValue = completedPoints!!.toInt()



        if (questionType.toInt() == 1) {
            if (clickedPos != finalAnswer.toInt()) {
                poiChallengeXP = 0

                quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"
            } else if (clickedPos == finalAnswer.toInt()) {
                poiChallengeXP = 50

                quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"
            } else {
                poiChallengeXP =
                    sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()

                quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"
            }
        } else if (questionType.toInt() == 2) {
            var count: Int = 0

            for (i in 0 until finalAnswerObj.size()) {

                if (finalAnswerObj[i].asInt == clickedAnswerArrayList[i].toInt()) {
                    ++count
                }

            }
            poiChallengeXP *= count
            quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"

        }

        val totalXP: Int = poiDiscoverXP + poiChallengeXP + completedPointIntValue
        quiz_challenge_screen_discovery_value.text =
            "+${sharedPreference.getSession("selectedPOIDiscovery_XP_Value")} XP"
        quiz_challenge_level_name.text = presentLevel
        var balanceVal: Int = progressBarMaxValue - totalXP
        if (balanceVal < 0) {
            progressBarMaxValue += 2000
            balanceVal = progressBarMaxValue - totalXP
            levelValue = "LV ${sharedPreference.getIntegerSession("lv_value_int")}"
        }

        quizBalanceValue.text = "$balanceVal XP TO $levelValue"

        sharedPreference.saveSession("xp_balance_value", balanceVal)
        sharedPreference.saveSession("total_gained_xp", totalXP)
        sharedPreference.saveSession("balance_xp_string", quizBalanceValue.text.toString())


        determinateBar.max = progressBarMaxValue
        ObjectAnimator.ofInt(determinateBar, "progress", totalXP)
            .setDuration(1000)
            .start()


    }

    private fun calculateUserLevel(exp: Int) {
        var ranking: String = ""
        var level: Int = 0
        var base: Int = 0
        var nextLevel: Int = 0
        when (exp) {
            in 0..999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 1
                base = 1000
                nextLevel = 2000
            }
            in 1000..1999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 2
                base = 1000
                nextLevel = 2000
            }
            in 2000..2999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 3
                base = 2000
                nextLevel = 3000
            }
            in 3000..3999 -> { // 1000 thresh
                ranking = "RECRUIT"
                level = 4
                base = 3000
                nextLevel = 4000
            }
            in 4000..5999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 5
                base = 4000
                nextLevel = 6000
            }
            in 6000..7999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 6
                base = 6000
                nextLevel = 8000
            }
            in 8000..9999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 7
                base = 8000
                nextLevel = 10000
            }
            in 10000..11999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 8
                base = 10000
                nextLevel = 12000
            }
            in 12000..13999 -> { // 2000 thresh
                ranking = "RECRUIT"
                level = 9
                base = 12000
                nextLevel = 14000
            }
            in 14000..16999 -> { // 2000 thresh
                ranking = "NAVIGATOR"
                level = 10
                base = 14000
                nextLevel = 17000

            }
            in 17000..20499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 11
                base = 17000
                nextLevel = 20500

            }
            in 20500..24499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 12
                base = 20500
                nextLevel = 24500

            }
            in 24500..28499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 13
                base = 24500
                nextLevel = 28500

            }
            in 28500..33499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 14
                base = 28500
                nextLevel = 33500

            }
            in 33500..38999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 15
                base = 33500
                nextLevel = 39000

            }
            in 39000..44999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 16
                base = 39000
                nextLevel = 45000

            }
            in 45000..51499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 17
                base = 45000
                nextLevel = 51500

            }
            in 51500..58499 -> { // 2000 thresh
                ranking = "Navigator"
                level = 18
                base = 51500
                nextLevel = 58500

            }
            in 58500..65999 -> { // 2000 thresh
                ranking = "Navigator"
                level = 19
                base = 58500
                nextLevel = 66000

            }
            in 66000..73999 -> { // 2000 thresh
                ranking = "Captain"
                level = 20
                base = 66000
                nextLevel = 74000

            }
        }
        println("Quiz Challenge => $ranking $level")
        quiz_challenge_level_name.text = "$ranking $level"
        val expToLevel =
            (nextLevel - base) - (exp - base) // (2000-1000) - (400-1000) = (1000)-(-600)=1600
        println("Quiz Challenge => expToLevel= $expToLevel")

        val poiDiscoverXP: Int =
            sharedPreference.getSession("selectedPOIDiscovery_XP_Value")!!.toInt()
        var poiChallengeXP: Int =
            sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()


        if (questionType.toInt() == 1) {
            if (clickedPos != finalAnswer.toInt()) {
                poiChallengeXP = 0
                println("Question XP Details : clickedPos = IF  $clickedPos ${finalAnswer.toInt()} == $poiChallengeXP")
                quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"
            } else if (clickedPos == finalAnswer.toInt()) {
                poiChallengeXP =
                    sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()
                println("Question XP Details : clickedPos = ELSE IF $clickedPos ${finalAnswer.toInt()} == $poiChallengeXP")
                quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"
            } else {
                poiChallengeXP =
                    sharedPreference.getSession("selectedPOIChallenge_XP_Value")!!.toInt()
                println("Question XP Details : clickedPos = ELSE  $clickedPos ${finalAnswer.toInt()} == $poiChallengeXP")
                quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"
            }
        } else if (questionType.toInt() == 2) {
            var count: Int = 0
            println("Question XP Details : Final Answers : $clickedAnswerArrayList <> $finalAnswerObj")
            for (i in 0 until finalAnswerObj.size()) {

                if (finalAnswerObj[i].asInt == clickedAnswerArrayList[i].toInt()) {
                    ++count
                }

            }
            poiChallengeXP *= count
            quiz_challenge_screen_quiz_answer_score_tv.text = "+$poiChallengeXP XP"
            println("Question XP Details : count Answers : $count")
        }
        val totalXP: Int = poiDiscoverXP + poiChallengeXP + exp
        println("Quiz Challenge => Report => totalXP= $poiDiscoverXP + $poiChallengeXP + $exp = $totalXP")
        val balanceVal: Int = nextLevel - totalXP
        println("Quiz Challenge => Report => balanceVal= $nextLevel - $totalXP = $balanceVal")

        quizBalanceValue.text = " $balanceVal XP TO Lv ${level + 1}"

        determinateBar.max = nextLevel
        ObjectAnimator.ofInt(
            determinateBar,
            "progress",
            totalXP
        )
            .setDuration(1000)
            .start()

        println("beginChallenge Input = Calc = $poiChallengeXP")
        beginChallengeAPI(finalAnswer, poiChallengeXP.toString())
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

                            println("GetUseDetails = ${CommonData.getUserDetails!!.experience}")


                            runOnUiThread {
                                println("From Get User :: ${Integer.parseInt(CommonData.getUserDetails!!.experience)}")
                                calculateUserLevel(Integer.parseInt(CommonData.getUserDetails!!.experience))
                            }


                        } else {

                        }
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

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