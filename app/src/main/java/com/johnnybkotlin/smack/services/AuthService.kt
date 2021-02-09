package com.johnnybkotlin.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.johnnybkotlin.smack.utility.URL_LOGIN
import com.johnnybkotlin.smack.utility.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var userPassword = ""
    var authToken = ""

    fun registerUser(context: Context,email:String,password:String,complete : (Boolean) -> Unit){

        val jsonBody = JSONObject()

        jsonBody.put("email",email)
        jsonBody.put("password",password)

        val requestBody = jsonBody.toString()

        val registerRequest = object  :StringRequest(Method.POST, URL_REGISTER,
                Response.Listener { response ->

                    Log.v("Registerresponse",response.toString())
                    complete(true)
                },
                Response.ErrorListener { error ->

                    Log.v("RegisterError"," Register error "+error.message)
                    complete(false)
                }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }


    fun loginUser(context: Context,email: String,password: String,complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()

        jsonBody.put("email",email)
        jsonBody.put("password",password)

        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST,URL_LOGIN, null,
                Response.Listener { response ->
                    Log.v("Loginresponse",response.toString())
                    try {
                        userEmail = response.getString("user")
                        authToken = response.getString("token")
                        isLoggedIn = true
                        complete(true)
                    }catch (e : JSONException){
                        e.printStackTrace()
                        Log.v("Loginresponse"," "+e.message.toString())
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    userEmail = ""
                    authToken = ""
                    isLoggedIn = false
                    Log.v("Loginresponse",error.message.toString())
                    complete(true)
                }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        Volley.newRequestQueue(context).add(loginRequest)
    }
}