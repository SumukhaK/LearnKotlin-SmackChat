package com.johnnybkotlin.smack.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.johnnybkotlin.smack.R
import com.johnnybkotlin.smack.services.AuthService
import com.johnnybkotlin.smack.services.UserDataService
import com.johnnybkotlin.smack.utility.BROADCAST_USERDATA_CHANGED
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_create_user.createEmailtext
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    val TAG ="LOGIN_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnClicked(view: View) {

        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        if (password.isNotEmpty() && email.isNotEmpty()){
            enableSpinner(true)
            AuthService.loginUser(this, email, password){
                complete ->
                if(complete){
                  AuthService.findUserByMail(this){ findUsercomplete ->

                      if(findUsercomplete){
                          Log.v(TAG, "findUserByMail : " + complete)
                          enableSpinner(false)
                          Toast.makeText(this," ${UserDataService.name} logged in Successfully !.. ",Toast.LENGTH_LONG).show()
                          val userDataChanged = Intent(BROADCAST_USERDATA_CHANGED)
                          LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChanged)
                          finish()
                      }else{
                          errorToast()
                          Log.v(TAG, "findUserByMail : " + complete)
                      }
                  }
                    Log.v(TAG, "loginComplete : " + complete)

                }else{
                    errorToast()
                    Log.v(TAG, "loginComplete : " + complete)
                }

            }
        }

    }
    fun createuserbtnclick(view: View) {

        val createUserIntent = Intent(this,CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun enableSpinner(enable:Boolean){

        if(enable){
            loginSpinner.visibility = View.VISIBLE
        }else{
            loginSpinner.visibility = View.INVISIBLE
        }
        login_button.isEnabled = !enable
        logincreateuserbtn.isEnabled = !enable
    }

    fun errorToast(){
        Toast.makeText(this,"Something went wrong, Please try again !..", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }
}