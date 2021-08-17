package com.breadcrumbsapp.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.ExpandableListAdapter
import com.breadcrumbsapp.databinding.FaqLayoutBinding
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.faq_layout.*


class FAQActivity : AppCompatActivity() {

    private lateinit var expandableListAdapter: ExpandableListAdapter

    var listDataHeader: ArrayList<String>? = null

    private lateinit var binding: FaqLayoutBinding

    var question1:Int=0
    var question2:Int=0
    var question3:Int=0
    var question4:Int=0
    var question5:Int=0
    var question6:Int=0
    var question7:Int=0
    var question8:Int=0
    var question9:Int=0
    var question10:Int=0
    var question11:Int=0
    var question12:Int=0
    var question13:Int=0
    var question14:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FaqLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        faq_screen_backButton.setOnClickListener(View.OnClickListener {
            finish()
        })


        faq1_question_layout.setOnClickListener(View.OnClickListener {
            if(question1==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_1)
                faq1_answer_tv.visibility=View.VISIBLE
                question1=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_1)
                faq1_answer_tv.visibility=View.GONE
                question1=0
            }

        })

        faq2_question_layout.setOnClickListener(View.OnClickListener {
            if(question2==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_2)
                faq2_answer_tv.visibility=View.VISIBLE
                question2=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_2)
                faq2_answer_tv.visibility=View.GONE
                question2=0
            }
        })
        faq3_question_layout.setOnClickListener(View.OnClickListener {
            if(question3==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_3)
                faq3_answer_tv.visibility=View.VISIBLE
                question3=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_3)
                faq3_answer_tv.visibility=View.GONE
                question3=0
            }
        })
        faq4_question_layout.setOnClickListener(View.OnClickListener {
            if(question4==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_4)
                faq4_answer_tv.visibility=View.VISIBLE
                question4=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_4)
                faq4_answer_tv.visibility=View.GONE
                question4=0
            }
        })
        faq5_question_layout.setOnClickListener(View.OnClickListener {
            if(question5==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_5)
                faq5_answer_tv.visibility=View.VISIBLE
                question5=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_5)
                faq5_answer_tv.visibility=View.GONE
                question5=0
            }
        })
        faq6_question_layout.setOnClickListener(View.OnClickListener {
            if(question6==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_6)
                faq6_answer_tv.visibility=View.VISIBLE
                question6=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_6)
                faq6_answer_tv.visibility=View.GONE
                question6=0
            }
        })
        faq7_question_layout.setOnClickListener(View.OnClickListener {
            if(question7==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_7)
                faq7_answer_tv.visibility=View.VISIBLE
                question7=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_7)
                faq7_answer_tv.visibility=View.GONE
                question7=0
            }
        })
        faq8_question_layout.setOnClickListener(View.OnClickListener {
            if(question8==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_8)
                faq8_answer_tv.visibility=View.VISIBLE
                question8=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_8)
                faq8_answer_tv.visibility=View.GONE
                question8=0
            }
        })
        faq9_question_layout.setOnClickListener(View.OnClickListener {
            if(question9==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_9)
                faq9_answer_tv.visibility=View.VISIBLE
                question9=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_9)
                faq9_answer_tv.visibility=View.GONE
                question9=0
            }
        })
        faq10_question_layout.setOnClickListener(View.OnClickListener {
            if(question10==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_10)
                faq10_answer_tv.visibility=View.VISIBLE
                question10=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_10)
                faq10_answer_tv.visibility=View.GONE
                question10=0
            }
        })
        faq11_question_layout.setOnClickListener(View.OnClickListener {
            if(question11==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_11)
                faq11_answer_tv.visibility=View.VISIBLE
                question11=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_11)
                faq11_answer_tv.visibility=View.GONE
                question11=0
            }
        })
        faq12_question_layout.setOnClickListener(View.OnClickListener {
            if(question12==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_12)
                faq12_answer_tv.visibility=View.VISIBLE
                question12=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_12)
                faq12_answer_tv.visibility=View.GONE
                question12=0
            }
        })
        faq13_question_layout.setOnClickListener(View.OnClickListener {
            if(question13==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_13)
                faq13_answer_tv.visibility=View.VISIBLE
                question13=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_13)
                faq13_answer_tv.visibility=View.GONE
                question13=0
            }
        })
        faq14_question_layout.setOnClickListener(View.OnClickListener {
            if(question14==0)
            {
                Glide.with(applicationContext).load(R.drawable.faq_up_arrow).into(expand_btn_14)
                faq14_answer_tv.visibility=View.VISIBLE
                question14=1
            }
            else
            {
                Glide.with(applicationContext).load(R.drawable.faq_down_arrow).into(expand_btn_14)
                faq14_answer_tv.visibility=View.GONE
                question14=0
            }
        })

    }


}