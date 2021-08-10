package com.breadcrumbsapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.breadcrumbsapp.R
import androidx.fragment.app.add
class MainActivity :AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState==null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.fragment_container_view)

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}