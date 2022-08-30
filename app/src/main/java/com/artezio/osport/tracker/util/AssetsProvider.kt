package com.artezio.osport.tracker.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import java.io.IOException
import javax.inject.Inject

@ViewModelScoped
class AssetsProvider @Inject constructor(
    @ApplicationContext private val context: Context
)  {

    fun readJsonFileFromAssets(fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (ex: IOException) {
            Log.e("build_info", ex.message.toString())
            null
        }
    }
}