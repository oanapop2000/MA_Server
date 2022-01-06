package com.example.lab2.players.utils

import android.util.Log

fun Any.logd(message: Any? = "no message!", cause: Throwable? = null) {
  Log.d(this.javaClass.simpleName, message.toString(), cause)
}