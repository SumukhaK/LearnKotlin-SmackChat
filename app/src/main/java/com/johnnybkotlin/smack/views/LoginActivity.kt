package com.johnnybkotlin.smack.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.johnnybkotlin.smack.R
import com.johnnybkotlin.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_create_user.createEmailtext
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnClicked(view: View) {

        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        if (password.isNotEmpty() && email.isNotEmpty()){

            AuthService.registerUser(this, createEmailtext.text.toString(), createPasswordtext.text.toString()){
                complete ->

                if(complete){


                }else{


                }

            }
        }

    }
    fun createuserbtnclick(view: View) {

        val createUserIntent = Intent(this,CreateUserActivity::class.java)
        startActivity(createUserIntent)
    }
}