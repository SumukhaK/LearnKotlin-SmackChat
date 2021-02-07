package com.johnnybkotlin.smack.views

import android.content.Intent
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
    fun createuserbtnclick(view: View) {

        val createUserIntent = Intent(this,CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }
}