package com.johnnybkotlin.smack.services

import android.graphics.Color
import android.util.Log
import androidx.core.view.KeyEventDispatcher
import java.util.*

object UserDataService {

    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun returnAvatarColors(components: String) : Int{

        //Log.v("Avatarcomponents"," "+components)
        val strippedColor = components
                .replace("[","")
                .replace("]","")
                .replace(","," ")

        var r = 0
        var g = 0
        var b = 0
        var alpha = 0
        //Log.v("Avatarcomponents"," "+strippedColor)
        val scanner = Scanner(strippedColor)
        try{
            if(scanner.hasNext()){
                //Log.v("Avatarcomponents"," scanner.next() : "+scanner.next())
                //if(scanner.hasNextDouble()) {
                //Log.v("Avatarcomponents"," scanner.nextDouble() : "+scanner.nextDouble())
                r = (scanner.nextDouble() * 255).toInt()
                g = (scanner.nextDouble() * 255).toInt()
                b = (scanner.nextDouble() * 255).toInt()
                alpha = (scanner.nextDouble()).toInt()

                //}else{
                //    throw Throwable(Exception("End of Doubles Era"))
                //}

            }
        }catch (e:Exception){
            Log.v("AvatarE"," "+e.message)
            e.printStackTrace()
        }
        //Log.v("Avatarcomponents","r : ${r} g : $g b: $b alpha : $alpha")
        //Log.v("Avatarcomponents","Color : ${Color.rgb(r,g,b)}")
        return Color.rgb(r,g,b)
    }

    fun logOut(){

        var id = ""
        var avatarColor = ""
        var avatarName = ""
        var email = ""
        var name = ""
        AuthService.authToken = ""
        AuthService.isLoggedIn = false
        AuthService.userEmail = ""
    }
}