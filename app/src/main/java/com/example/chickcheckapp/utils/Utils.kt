package com.example.chickcheckapp.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.util.Log
import android.view.Surface
import androidx.camera.view.PreviewView
import java.io.File
import java.io.FileOutputStream

object Utils {
    const val TAG = "Utils"
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
    fun dialogAlertBuilder(context: Context,title:String, message: String, positive : ()->Unit): AlertDialog.Builder{
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setCancelable(true)
        builder.setPositiveButton("Yes"){ _, _ ->
            positive()
        }
        builder.setNegativeButton("No"){ dialog: DialogInterface?, which: Int->
            dialog?.cancel()
        }
        return builder
    }
    fun rotateImage(filePath: String,viewFinder:PreviewView) {
        try {
            val bitmap = BitmapFactory.decodeFile(filePath)
            val matrix = Matrix()

            // Get device rotation
            val rotation = viewFinder.display.rotation
            Log.d(TAG, "Device rotation: $rotation")

            // Rotate the image based on the phone's current rotation
            when (rotation) {
                Surface.ROTATION_0 -> matrix.postRotate(0f)
                Surface.ROTATION_90 -> matrix.postRotate(90f)
                Surface.ROTATION_180 -> matrix.postRotate(180f)
                Surface.ROTATION_270 -> matrix.postRotate(270f)
            }

            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            // Save the rotated image back to the file
            val file = File(filePath)
            FileOutputStream(file).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}