package com.johnnybkotlin.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.johnnybkotlin.smack.App
import com.johnnybkotlin.smack.model.Channel
import com.johnnybkotlin.smack.model.Message
import com.johnnybkotlin.smack.utility.URL_GET_CHANNELS
import com.johnnybkotlin.smack.utility.URL_GET_MESSAGES
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit){

        val getChannelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS,null,
            Response.Listener { response ->
                try {
                    for( i in 0 until response.length()) {
                        val channel = Channel(
                            response.getJSONObject(i).getString("name"),
                            response.getJSONObject(i).getString("description"),
                            response.getJSONObject(i).getString("_id")
                        )
                        this.channels.add(channel)
                    }
                    complete(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.v(AuthService.TAG +"JSON ", e.message.toString())
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                onErrorResponse(error)
                Log.v(AuthService.TAG +"RESPONSE ", error.message.toString())
                complete(false)
            }
        ){
            override fun getBodyContentType(): String {

                return "application/json; charset=utf-8"

            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${ App.Companion.sharedPreferences.authToken}")
                return headers
            }
        }
        App.sharedPreferences.requestQueue.add(getChannelsRequest)
    }

    fun getMessages(channelId:String,complete: (Boolean) -> Unit){

        val url = "$URL_GET_MESSAGES$channelId"
        Log.v(AuthService.TAG +"getMessages ", url)
        this.clearMesssages()
        val getMessagesRequest = object : JsonArrayRequest(Method.GET, url,null,
            Response.Listener { response ->
                try {
                    for( i in 0 until response.length()) {
                        val message = Message(
                            response.getJSONObject(i).getString("messageBody"),
                            response.getJSONObject(i).getString("userName"),
                            response.getJSONObject(i).getString("channelId"),
                            response.getJSONObject(i).getString("userAvatar"),
                            response.getJSONObject(i).getString("userAvatarColor"),
                            response.getJSONObject(i).getString("_id"),
                            response.getJSONObject(i).getString("timeStamp")
                        )
                        this.messages.add(message)
                    }
                    complete(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.v(AuthService.TAG +"JSON ", e.message.toString())
                    complete(false)
                }
            },
            Response.ErrorListener {
                    error ->
                error.printStackTrace()
                onErrorResponse(error)
                Log.v(AuthService.TAG +" MSG ", error.message.toString())
                complete(false)

            }
        ){
            override fun getBodyContentType(): String {

                return "application/json; charset=utf-8"

            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${ App.Companion.sharedPreferences.authToken}")
                return headers
            }
        }
        App.sharedPreferences.requestQueue.add(getMessagesRequest)
    }

    fun clearMesssages(){
        this.messages.clear()
    }

    fun clearChannels(){
        this.channels.clear()
    }

    fun onErrorResponse(error: VolleyError) {

        // As of f605da3 the following should work
        //Log.v(TAG, "Verror "+error.networkResponse.data.toString(UTF_8)+" "+error.networkResponse.statusCode)
        val response = error.networkResponse
        if (error is ServerError && response != null) {
            try {
                val errorByte = error.networkResponse.data
                val parseError =  errorByte.toString(Charsets.UTF_8)

                val errorObj = JSONObject(parseError)
                val errorMessage = errorObj.getString("message")
                Log.v(AuthService.TAG, errorMessage)
            } catch (e1: UnsupportedEncodingException) {
                // Couldn't properly decode data to string
                e1.printStackTrace()
            } catch (e2: JSONException) {
                // returned data is not JSONObject?
                e2.printStackTrace()
            }
        }
    }
}