package com.example.lab2


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

internal class Manager {

  @RequiresApi(Build.VERSION_CODES.M)
  fun networkConnectivity(context: Context): Boolean {
    var result = false
    val cm = context
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    cm.run {
      cm.getNetworkCapabilities(cm.activeNetwork)?.run {
        result = when {
          hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
          hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
          hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
          else -> false
        }
      }
    }
    return result
  }
}
