package com.example.chickcheckapp.utils

import android.annotation.SuppressLint
import android.location.Location

object Utils {
    fun turnIntoLocation(latitude: Double, longitude: Double): Location {
        val location = Location("")
        location.latitude = latitude
        location.longitude = longitude
        return location
    }
    @SuppressLint("DefaultLocale")
    fun convertDistance(distance: Float):String{
        return if(distance<1000){
            String.format("%.0f m", distance )
        }else{
            String.format("%.1f km", distance / 1000)
        }
    }
}