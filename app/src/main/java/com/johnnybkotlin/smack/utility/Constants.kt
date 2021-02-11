package com.johnnybkotlin.smack.utility

const val BASE_URL = "https://herokumongodbsmackchat.herokuapp.com/v1"
const val SOCKET_URL = "https://herokumongodbsmackchat.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}/account/register"
const val URL_LOGIN = "${BASE_URL}/account/login"
const val URL_ADDUSER = "${BASE_URL}/user/add"
const val URL_GETUSER = "${BASE_URL}/user/byEmail/"


//Broadcast constants
const val BROADCAST_USERDATA_CHANGED = "BROADCAST_USERDATA_CHANGED"