package com.breadcrumbsapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.databinding.ChallengeActivityBinding
import com.breadcrumbsapp.util.SessionHandlerClass
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.challenge_activity.*


class ChallengeActivity : AppCompatActivity() {

    lateinit var binding: ChallengeActivityBinding
    lateinit var sharedPreference: SessionHandlerClass

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChallengeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SessionHandlerClass(applicationContext)
        poiNameTextView.text = sharedPreference.getSession("selectedPOIName")


        val bundle: Bundle = intent.extras!!
        val challengeName = bundle.getString("challengeName")
        val poiImage = bundle.getString("poiImage")

        sharedPreference.saveSession("poi_image", poiImage.toString())

        Glide.with(applicationContext).load(poiImage).into(binding.selfieImageView)

        println("challengeName :: $challengeName")

        if (challengeName == "quiz") {
            challengeIcon.setImageDrawable(getDrawable(R.drawable.quiz_challenge_icon))
            challengeTitle.text = resources.getString(R.string.quiz_challenge)
            subTileOfBeginChallenge.text =
                resources.getString(R.string.quiz_challenge_static_content)
            questionTwoLabel.text = "Correct Answer"
        } else if (challengeName == "selfie") {
            challengeIcon.setImageDrawable(getDrawable(R.drawable.selfie_challenge_icon))
            challengeTitle.text = resources.getString(R.string.selfie_challenge)
            subTileOfBeginChallenge.text =
                resources.getString(R.string.selfie_challenge_static_content)
            questionTwoLabel.text = "Selfie Posted"
        }

        challenge_backButton.setOnClickListener {
            finish()
        }

        binding.beginButton.setOnClickListener {
            when (challengeName) {
                "quiz" -> {
                    startActivity(
                        Intent(
                            this@ChallengeActivity,
                            QuizChallengeQuestionActivity::class.java
                        ).putExtra("poiImage", poiImage)
                    )
                }
                "selfie" -> {
                    startActivity(
                        Intent(
                            this@ChallengeActivity,
                            com.breadcrumbsapp.camerafiles.fragments.MainActivity::class.java
                        )
                    )
                }
            }
        }

    }
}