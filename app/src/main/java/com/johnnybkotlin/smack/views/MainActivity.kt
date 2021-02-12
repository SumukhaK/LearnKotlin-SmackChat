package com.johnnybkotlin.smack.views

import android.R.attr.data
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.IO
import com.johnnybkotlin.smack.App
import com.johnnybkotlin.smack.R
import com.johnnybkotlin.smack.model.Channel
import com.johnnybkotlin.smack.model.Message
import com.johnnybkotlin.smack.services.AuthService
import com.johnnybkotlin.smack.services.MessageService
import com.johnnybkotlin.smack.services.UserDataService
import com.johnnybkotlin.smack.utility.BROADCAST_USERDATA_CHANGED
import com.johnnybkotlin.smack.utility.SOCKET_URL
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    val TAG = "MainActiityLOGS"
    var selectedChannel: Channel? = null
    lateinit var channelAdapter:ArrayAdapter<Channel>

    private fun setAdapter(){
        channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,MessageService.channels)
        channel_list.adapter = channelAdapter
    }

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
        hideKeyboard()
        enableSpinner(true)
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangedReciever, IntentFilter(BROADCAST_USERDATA_CHANGED))
        socket.connect()
        socket.on("channelCreated",onNewChannel)
        socket.on("messageCreated",onNewMessage)
        setAdapter()
        Log.v(TAG," App.sharedPreferences.isLoggedIn ${App.sharedPreferences.isLoggedIn}")
        if(App.sharedPreferences.isLoggedIn){

            AuthService.findUserByMail(){ complete ->
                if(complete){
                    enableSpinner(false)
                    Log.v(TAG," findUserByMail complete $complete")
                    val userDataChanged = Intent(BROADCAST_USERDATA_CHANGED)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChanged)
                }else{
                    errorToast()
                    Log.v(TAG," findUserByMail complete $complete")
                }

            }
        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        channel_list.setOnItemClickListener{adapterview,view,i,l ->

            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }
    }

    override fun onResume() {
        super.onResume()
        if(App.sharedPreferences.isLoggedIn){
            enableSpinner(false)
        }
        /*socket.connect()
        socket.on("channelCreated",onNewChannel)*/
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangedReciever)
    }

    private val userDataChangedReciever = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
            //TODO("Not yet implemented")
            if(App.sharedPreferences.isLoggedIn){
                username_navheader.text = UserDataService.name
                useremail_navheader.text = UserDataService.email
                mainChannelName.text = getString(R.string.text_add_channel)
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userimage_navheader.setImageResource(resourceId)
                //Log.v("Avatarcomponents"," "+UserDataService.avatarColor)
                //Log.v("AvatarcompActivity"," "+UserDataService.returnAvatarColors(UserDataService.avatarColor))
                userimage_navheader.setBackgroundColor(UserDataService.returnAvatarColors(UserDataService.avatarColor))
                login_navheader_button.text = getString(R.string.text_logout)

                MessageService.getChannels(){
                        complete ->
                    if(complete){
                        Log.v(TAG," getChannels complete $complete")
                        if(MessageService.channels.count()>0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }else{
                        Log.v(TAG," getChannels complete $complete")

                    }
                }
            }
        }
    }


    fun updateWithChannel(){

        mainChannelName.text = "#${selectedChannel?.name}"
        //TODO: download the messages from the channel
    }

    fun loginBtnNavClicked(view: View) {

        if(App.sharedPreferences.isLoggedIn){
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

    fun addChannelBtnClicked(view: View) {

        if(App.sharedPreferences.isLoggedIn){

            val builder = AlertDialog.Builder(this)
            val dialogueView = layoutInflater.inflate(R.layout.add_channel_dialogue, null)

            builder.setView(dialogueView)
                .setPositiveButton("Add"){ dialogInterface, i ->

                    val nameTextfield = dialogueView.findViewById<EditText>(R.id.addChannelNameText)
                    val descTextfield = dialogueView.findViewById<EditText>(R.id.descriptionText)
                    val channelName = nameTextfield.text.toString()
                    val channelDesc = descTextfield.text.toString()
                    Log.v(TAG," newChannelDetails $channelName $channelDesc")
                    //socket.emit("newChannel",channelName,channelDesc,Ack)
                    socket.emit("newChannel", channelName,channelDesc, Ack { args ->
                        val repues = args[0] as JSONObject
                        Log.v(TAG," repupes "+repues.toString())
                        Log.v(TAG," args "+args.toString())
                    })

                    hideKeyboard()
                }
                .setNegativeButton("Cancel"){ dialogInterface, i ->

                    dialogInterface.dismiss()
                    hideKeyboard()
                }.show()

        }


    }

    fun sendmessageOnclick(view: View) {

        if(App.sharedPreferences.isLoggedIn && messagetextfield.text.isNotEmpty()){

            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage",messagetextfield.text.toString(),userId,channelId,UserDataService.name,UserDataService.avatarName,UserDataService.avatarColor, Ack { args ->
                val repues = args[0] as JSONObject
                Log.v(TAG," newMessage repupes "+repues.toString())
                Log.v(TAG," newMessage args "+args.toString())
            })
            messagetextfield.text.clear()
            hideKeyboard()
        }

    }

    fun hideKeyboard(){

        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private val onNewChannel =Emitter.Listener { args ->

        runOnUiThread {
            val channelName = args[0] as String
            val channelDesc = args[1] as String
            val channelID = args[2] as String
            Log.v(TAG," newChannel $channelName $channelDesc")
            val newChannel = Channel(channelName,channelDesc,channelID)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    private val onNewMessage =Emitter.Listener { args ->

        runOnUiThread {
            val messageBody = args[0] as String
            val channelId = args[2] as String
            val userName = args[3] as String
            val userAvatar = args[4] as String
            val userAvatarColor = args[5] as String
            val id = args[6] as String
            val timeStamp = args[7] as String

            val newMessage = Message(messageBody,userName,channelId,userAvatar,userAvatarColor,id,timeStamp)
            MessageService.messages.add(newMessage)
            Log.v(TAG," newMessage ${newMessage.message} ${newMessage.userName} ")
        }
    }

    fun enableSpinner(enable:Boolean){

        if(enable){
            mainProgressbar.visibility = View.VISIBLE
            hideKeyboard()
            sendmessagebtn.isEnabled = false
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }else{
            mainProgressbar.visibility = View.INVISIBLE
            sendmessagebtn.isEnabled = true
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }

    }

    fun errorToast(){
        Toast.makeText(this,"Something went wrong, Please Log in again!..", Toast.LENGTH_LONG).show()
        enableSpinner(false)
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}