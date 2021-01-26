package com.johnnybkotlin.smack.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.johnnybkotlin.smack.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnClicked(view: View) {}
    fun createuserbtnclick(view: View) {}
}