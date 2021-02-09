package com.johnnybkotlin.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.johnnybkotlin.smack.utility.URL_ADDUSER
import com.johnnybkotlin.smack.utility.URL_LOGIN
import com.johnnybkotlin.smack.utility.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var userPassword = ""
    var authToken = ""
    val TAG ="API_TAG"

    fun registerUser(context: Context,email:String,password:String,complete : (Boolean) -> Unit){

        val jsonBody = JSONObject()

        jsonBody.put("email",email)
        jsonBody.put("password",password)

        val requestBody = jsonBody.toString()

        val registerRequest = object  :StringRequest(Method.POST, URL_REGISTER,
                Response.Listener { response ->

                    Log.v(TAG,response.toString())
                    complete(true)
                },
                Response.ErrorListener { error ->

                    Log.v(TAG," Register error "+error.localizedMessage)
                    error.printStackTrace()
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
                    Log.v(TAG,response.toString())
                    try {
                        userEmail = response.getString("user")
                        authToken = response.getString("token")
                        isLoggedIn = true
                        complete(true)
                    }catch (e : JSONException){
                        e.printStackTrace()
                        Log.v(TAG," "+e.message.toString())
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    userEmail = ""
                    authToken = ""
                    isLoggedIn = false
                    Log.v(TAG,error.message.toString())
                    complete(false)
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

    fun createUser(context: Context,name:String,email:String,avatarName:String,avatarColor:String,complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()

        jsonBody.put("name",name)
        jsonBody.put("email",email)
        jsonBody.put("avatarName",avatarName)
        jsonBody.put("avatarColor",avatarColor)

        val createUserBody = jsonBody.toString()

        val addUserRequest = object : JsonObjectRequest(Method.POST, URL_ADDUSER,null,
                Response.Listener { response ->

                    try{
                        UserDataService.id = response.getString("_id")
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        complete(true)
                    }catch (e : JSONException){
                        e.printStackTrace()
                        Log.v(TAG,e.message.toString())
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    Log.v(TAG,error.message.toString())
                    complete(false)
                }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return createUserBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer $authToken")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(addUserRequest)
    }
}