package com.johnnybkotlin.smack.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
            hideKeyboard()
            AuthService.loginUser( email, password){
                complete ->
                if(complete){
                    AuthService.findUserByMail(){ findUsercomplete ->

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


    fun hideKeyboard(){

        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

 /*   <!--        android:usesCleartextTraffic="true"-->
    <!--        android:networkSecurityConfig="@xml/network_security_config"-->*/
}