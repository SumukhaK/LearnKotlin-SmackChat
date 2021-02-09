package com.johnnybkotlin.smack.views

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.johnnybkotlin.smack.R
import com.johnnybkotlin.smack.services.AuthService
import com.johnnybkotlin.smack.services.UserDataService
import com.johnnybkotlin.smack.utility.BROADCAST_USERDATA_CHANGED
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangedReciever, IntentFilter(BROADCAST_USERDATA_CHANGED))

    }

    private val userDataChangedReciever = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            //TODO("Not yet implemented")
            if(AuthService.isLoggedIn){
                username_navheader.text = UserDataService.name
                useremail_navheader.text = UserDataService.email
                mainChannelName.text = getString(R.string.text_add_channel)
                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                userimage_navheader.setImageResource(resourceId)
                //Log.v("Avatarcomponents"," "+UserDataService.avatarColor)
                //Log.v("AvatarcompActivity"," "+UserDataService.returnAvatarColors(UserDataService.avatarColor))
                userimage_navheader.setBackgroundColor(UserDataService.returnAvatarColors(UserDataService.avatarColor))
                login_navheader_button.text = getString(R.string.text_logout)
            }
        }
    }


    fun loginBtnNavClicked(view: View) {

        if(AuthService.isLoggedIn){
            UserDataService.logOut()
            username_navheader.text = "LogIn"
            login_navheader_button.text = "LogIn"
            useremail_navheader.text = ""
            userimage_navheader.setImageResource(R.drawable.profiledefault)
            mainChannelName.text = getString(R.string.please_log_in)
            userimage_navheader.setBackgroundColor(Color.TRANSPARENT)
        }else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelBtnClicked(view: View) {}

    fun sendmessageOnclick(view: View) {}

}