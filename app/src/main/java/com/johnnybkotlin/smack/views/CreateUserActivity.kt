package com.johnnybkotlin.smack.views

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.johnnybkotlin.smack.R
import com.johnnybkotlin.smack.services.AuthService
import com.johnnybkotlin.smack.services.UserDataService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*


class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"
    val TAG ="CREATEUSER_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
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
        AuthService.registerUser(this,createEmailtext.text.toString(),createPasswordtext.text.toString()) { complete ->

            if (complete) {

                AuthService.loginUser(this,email,password){
                    loginComplete ->

                    if(loginComplete){
                        Log.v(TAG,"loginComplete : "+loginComplete)
                        Log.v(TAG," "+complete)
                        AuthService.createUser(this,userName,email,userAvatar,avatarColor){

                            addUserComplete ->

                            if(addUserComplete){
                                Log.v(TAG,"AddUserComplete : "+addUserComplete)
                                println("UserName : "+UserDataService.avatarName+" UserEmail : "+UserDataService.email)
                                finish()
                            }else{
                                Log.v(TAG,"AddUserComplete : "+addUserComplete)
                            }
                        }
                    }else{
                        Log.v(TAG,"loginComplete : "+loginComplete)
                    }

                }



                /*AuthService.loginUser(this,email,password){
                    loginComplete ->

                    if(loginComplete){
                        Log.v("RegisterActivity","loginComplete : "+loginComplete)
                    }else{
                        Log.v("RegisterActivity","loginComplete : "+loginComplete)
                    }

                }*/
            }

        }}
}