package com.johnnybkotlin.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.johnnybkotlin.smack.App
import com.johnnybkotlin.smack.utility.URL_ADDUSER
import com.johnnybkotlin.smack.utility.URL_GETUSER
import com.johnnybkotlin.smack.utility.URL_LOGIN
import com.johnnybkotlin.smack.utility.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import kotlin.text.Charsets.UTF_8


object AuthService {

//    var isLoggedIn = false
//    var userEmail = ""
//    var userPassword = ""
//    var authToken = ""
    val TAG ="API_TAG"

    fun registerUser(email: String, password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()

        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        val registerRequest = object  :StringRequest(Method.POST, URL_REGISTER,
                Response.Listener { response ->

                    Log.v(TAG, response.toString())
                    complete(true)
                },
                Response.ErrorListener { error ->

                    Log.v(TAG, " Register error " + error.localizedMessage)
                    onErrorResponse(error)
                    error.printStackTrace()
                    complete(false)
                }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
                //return "text/html; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.sharedPreferences.requestQueue.add(registerRequest)
    }

    fun loginUser(email: String, password: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()

        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
                Response.Listener { response ->
                    Log.v(TAG, response.toString())
                    try {
                        App.Companion.sharedPreferences.userEmail = response.getString("user")
                        App.Companion.sharedPreferences.authToken = response.getString("token")
                        App.Companion.sharedPreferences.isLoggedIn = true
                        //Log.v(TAG, " email : $userEmail Token : $authToken isLoggedIn : $isLoggedIn")
                        complete(true)
                    } catch (e: JSONException) {

                        e.printStackTrace()
                        Log.v(TAG, " " + e.message.toString())
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    App.Companion.sharedPreferences.userEmail = ""
                    App.Companion.sharedPreferences.authToken = ""
                    App.Companion.sharedPreferences.isLoggedIn= false
                    onErrorResponse(error)
                    Log.v(TAG, error.message.toString())
                    complete(false)
                }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        App.sharedPreferences.requestQueue.add(loginRequest)
    }

    fun createUser( name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()

        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)

        val createUserBody = jsonBody.toString()

        val addUserRequest = object : JsonObjectRequest(Method.POST, URL_ADDUSER, null,
                Response.Listener { response ->

                    try {
                        UserDataService.id = response.getString("_id")
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        complete(true)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.v(TAG, e.message.toString())
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    onErrorResponse(error)
                    Log.v(TAG, error.message.toString())
                    complete(false)
                }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return createUserBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.Companion.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(addUserRequest)
    }

    fun findUserByMail(complete: (Boolean) -> Unit){

        val findUserReq =object :JsonObjectRequest(Method.GET, URL_GETUSER+App.Companion.sharedPreferences.userEmail,null,

                Response.Listener { response ->
                    //Log.v(TAG+"URL", URL_GETUSER+userEmail)
                    //Log.v(TAG+"URL", response.toString())
                    try {
                        UserDataService.id = response.getString("_id")
                        UserDataService.name = response.getString("name")
                        UserDataService.email = response.getString("email")
                        UserDataService.avatarName = response.getString("avatarName")
                        UserDataService.avatarColor = response.getString("avatarColor")
                        complete(true)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.v(TAG+"JSON ", e.message.toString())
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    onErrorResponse(error)
                    Log.v(TAG+"RESPONSE ", error.message.toString())
                    complete(false)
                }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }


            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.Companion.sharedPreferences.authToken}")
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(findUserReq)
    }

    fun onErrorResponse(error: VolleyError) {

        // As of f605da3 the following should work
        //Log.v(TAG, "Verror "+error.networkResponse.data.toString(UTF_8)+" "+error.networkResponse.statusCode)
        val response = error.networkResponse
        if (error is ServerError && response != null) {
            try {
                val errorByte = error.networkResponse.data
                val parseError =  errorByte.toString(UTF_8)

                val errorObj = JSONObject(parseError)
                val errorMessage = errorObj.getString("message")
                Log.v(TAG, errorMessage)
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