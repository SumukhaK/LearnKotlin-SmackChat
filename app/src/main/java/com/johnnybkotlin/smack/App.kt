package com.johnnybkotlin.smack

import android.app.Application
import android.content.SharedPreferences
import com.johnnybkotlin.smack.utility.SahredPref

class App : Application() {

    companion object{

        lateinit var sharedPreferences: SahredPref
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = SahredPref(applicationContext)


    }
}