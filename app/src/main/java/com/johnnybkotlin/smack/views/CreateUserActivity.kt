package com.johnnybkotlin.smack.views

import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import java.util.*


class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"
    val TAG ="CREATEUSER_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if(color == 1){
            userAvatar = "light$avatar"

        }else{
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar,"drawable",packageName)
        createAvatarImageview.setImageResource(resourceId)
    }

    fun generateColoronClick(view: View) {

        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImageview.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR,$savedG,$savedB,1]"
    }

    fun createUserOnClick(view: View) {

        val email = createEmailtext.text.toString()
        val userName = createUserNameText.text.toString()
        val password = createPasswordtext.text.toString()

        if (userName.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()){
            enableSpinner(true)
            hideKeyboard()
            AuthService.registerUser( email, password) { complete ->

                if (complete) {
                    Toast.makeText(this,"Registration complete !!..",Toast.LENGTH_LONG).show()
                    AuthService.loginUser( email, password) { loginComplete ->

                        if (loginComplete) {
                            Log.v(TAG, "loginComplete : " + loginComplete)
                            Log.v(TAG, " " + complete)
                            AuthService.createUser(userName, email, userAvatar, avatarColor) {

                                addUserComplete ->

                                if (addUserComplete) {
                                    Log.v(TAG, "AddUserComplete : " + addUserComplete)
                                    val userDataChanged = Intent(BROADCAST_USERDATA_CHANGED)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChanged)
                                    println("UserName : " + UserDataService.avatarName + " UserEmail : " + UserDataService.email)
                                    finish()
                                } else {
                                    errorToast()
                                    Log.v(TAG, "AddUserComplete : " + addUserComplete)
                                }
                            }
                        } else {
                            errorToast()
                            Log.v(TAG, "loginComplete : " + loginComplete)
                        }
                    }
                } else {
                    errorToast()
                }

            }
        }else{
            Toast.makeText(this,"Make sure email, password and username are not empty !!..",Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }

    }

    fun enableSpinner(enable:Boolean){

        if(enable){
            createSpinner.visibility = View.VISIBLE
        }else{
            createSpinner.visibility = View.INVISIBLE
        }
        createUserBtn.isEnabled = !enable
        createAvatarImageview.isEnabled = !enable
        backgroundColorBtn.isEnabled = !enable
    }

    fun hideKeyboard(){

        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun errorToast(){

        Toast.makeText(this,"Something went wrong, Please try again !..",Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }
}