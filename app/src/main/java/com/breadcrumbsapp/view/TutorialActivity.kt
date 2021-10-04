package com.breadcrumbsapp.view

import android.app.Activity
import android.content.Intent
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbsapp.R
import com.breadcrumbsapp.adapter.TutorialScreenAdapter
import com.breadcrumbsapp.databinding.TutorialLayoutBinding
import com.breadcrumbsapp.util.SessionHandlerClass


class TutorialActivity : AppCompatActivity() {

    private lateinit var tutorialScreenAdapter: TutorialScreenAdapter
    private lateinit var binding: TutorialLayoutBinding
    private lateinit var sharedPreference: SessionHandlerClass
    private fun RecyclerView.getCurrentPosition(): Int {
        return (this.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TutorialLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SessionHandlerClass(this)
        // Object Initialization.,
        tutorialScreenAdapter = TutorialScreenAdapter(this@TutorialActivity)

        binding.recyclerview.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.recyclerview.adapter = tutorialScreenAdapter


        // Helps to do pagination
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerview)


        getDeviceDPI()


        // Page Indicator.,

        binding.navigateButton.text=resources.getText(R.string.skip_text)

        binding.navigateButton.setOnClickListener {


            startActivity(Intent(this@TutorialActivity, LoginScreen::class.java))

            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
            finish()



        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (val position: Int = binding.recyclerview.getCurrentPosition()) {
                    0 -> {
                        println("recyclerview 0 = $position")
                        binding.navigateButton.text=resources.getText(R.string.skip_text)
                    }
                    1 -> {
                        println("recyclerview 1 = $position")
                        binding.navigateButton.text=resources.getText(R.string.skip_text)
                    }
                    2 -> {
                        println("recyclerview 2 = $position")
                        binding.navigateButton.text=resources.getText(R.string.skip_text)
                    }
                    3 -> {
                        println("recyclerview 3 = $position")
                        binding.navigateButton.text=resources.getText(R.string.done_text)
                    }
                }
            }
        })


       binding.indicator.attachToRecyclerView(binding.recyclerview  )

    }

    private fun getDeviceDPI()
    {
        println("Density Range => getDeviceDPI == ${resources.displayMetrics.densityDpi}")
        when ( resources.displayMetrics.densityDpi) {




        }



    }
}

