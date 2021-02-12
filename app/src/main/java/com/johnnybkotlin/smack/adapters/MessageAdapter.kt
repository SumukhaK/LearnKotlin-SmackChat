package com.johnnybkotlin.smack.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.johnnybkotlin.smack.R
import com.johnnybkotlin.smack.model.Message
import com.johnnybkotlin.smack.services.UserDataService
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter (val context: Context, val messages : ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(context,messages.get(position))
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userImage = itemView.findViewById<ImageView>(R.id.messageUserImage)
        val userName = itemView.findViewById<TextView>(R.id.messageUsernameLabel)
        val messageTimeStamp = itemView.findViewById<TextView>(R.id.timeStampLabel)
        val messageBody = itemView.findViewById<TextView>(R.id.messsageBodylabel)

        fun bindMessage(context: Context,message: Message){

            val resourceId = context.resources.getIdentifier(message.userAvatar,"drawable",context.packageName)
            userImage?.setImageResource(resourceId)
            userImage.setBackgroundColor(UserDataService.returnAvatarColors(message.userAvatarColor))
            userName.setTextColor( UserDataService.returnAvatarColors(message.userAvatarColor))
            userName.text = message.userName
            messageTimeStamp.text = returnDateString(message.timeStamp)


            messageBody.text = message.message
        }
    }
    fun returnDateString(isoString:String) : String {

//val isoFormatter : SimpleDateFormat("EEE, MMM d, ''yy")

        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault())
        isoFormatter.timeZone = TimeZone.getTimeZone("IST")
        var convertedDate = Date()
        try{
            convertedDate =  isoFormatter.parse(isoString)
        }catch (e: Exception){
            e.printStackTrace()

        }

        val outDate = SimpleDateFormat("EEE, MMM d, h:mm a, ''yyyy", Locale.getDefault())

        return  outDate.format(convertedDate)
    }

}