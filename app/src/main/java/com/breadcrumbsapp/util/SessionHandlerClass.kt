package com.breadcrumbsapp.util

import android.content.Context
import android.content.SharedPreferences
import com.breadcrumbsapp.model.GetUserModel

class SessionHandlerClass(context:Context)
{
    private val prefName="breadcrumbsPreferences"
    private val sharedPref:SharedPreferences=context.getSharedPreferences(prefName,Context.MODE_PRIVATE)


    fun saveSession(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, value)
        editor.apply()
    }




    fun getSession(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, "")
    }

    fun clearSession() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun saveBoolean(KEY_NAME: String, status: Boolean) {

        val editor: SharedPreferences.Editor = sharedPref.edit()

        editor.putBoolean(KEY_NAME, status)

        editor.apply()
    }
    fun getBoolean(KEY_NAME: String): Boolean {

        return sharedPref.getBoolean(KEY_NAME,false)

    }
}