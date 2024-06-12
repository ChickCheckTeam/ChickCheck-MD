package com.example.chickcheckapp.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.net.Uri
import android.util.Log
import android.view.Surface
import androidx.camera.view.PreviewView
import com.example.chickcheckapp.data.remote.response.DataItem
import com.example.chickcheckapp.data.remote.response.ErrorResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object Utils {
    const val TAG = "Utils"
    const val MAXIMAL_SIZE = 1000000
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
    fun dialogAlertBuilder(context: Context,title:String, message: String, positive : ()->Unit): MaterialAlertDialogBuilder{
        val builder =  MaterialAlertDialogBuilder(context)
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

            val rotation = viewFinder.display.rotation
            Log.d(TAG, "Device rotation: $rotation")

            when (rotation) {
                Surface.ROTATION_0 -> matrix.postRotate(0f)
                Surface.ROTATION_90 -> matrix.postRotate(90f)
                Surface.ROTATION_180 -> matrix.postRotate(180f)
                Surface.ROTATION_270 -> matrix.postRotate(270f)
            }

            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            val file = File(filePath)
            FileOutputStream(file).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun uriToFile(imageUri: Uri, context: Context):File{
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length : Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer,0,length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun File.reduceFileSize():File{
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality =100
        var streamLength : Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,compressQuality,bmpStream)
            val bmpPictByteArray = bmpStream.toByteArray()
            streamLength = bmpPictByteArray.size
            compressQuality -= 5
        }while (streamLength> MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG,compressQuality,FileOutputStream(file))
        return file
    }

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile("${System.currentTimeMillis()}", ".jpg", filesDir)
    }
     fun parseJsonToErrorMessage(jsonInString: String?): String {
        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
        return errorBody.message
    }

    fun parseJsonToDisease(jsonString: String): Disease{
        val gson = Gson()
        val disease = gson.fromJson(jsonString, Disease::class.java)
        return disease
    }

}