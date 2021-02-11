package com.johnnybkotlin.smack.utility

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley
import com.johnnybkotlin.smack.App

class SahredPref (context: Context){

    val PREF_NAME = "smackPref"
    val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME,0)
    val IS_LOGGEDIN ="is_loggedin"
    val AUTH_TOKEN ="authToken"
    val USER_EMAIL ="userEmail"

    var isLoggedIn:Boolean
        get() = pref.getBoolean(IS_LOGGEDIN,false)
        set(value) = pref.edit().putBoolean(IS_LOGGEDIN,value).apply()

    var authToken: String?
        get() = pref.getString(AUTH_TOKEN,"")
        set(value) = pref.edit().putString(AUTH_TOKEN,value).apply()

    var userEmail: String?
        get() = pref.getString(USER_EMAIL,"")
        set(value) = pref.edit().putString(USER_EMAIL,value).apply()

    val requestQueue = Volley.newRequestQueue(context)
}