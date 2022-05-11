package com.artezio.osport.tracker.data.gpx

import android.content.Context
import android.util.Log
import com.artezio.osport.tracker.domain.model.LocationPointData
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@ViewModelScoped
class GPX @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val header =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
    private val footer = "</trk></gpx>"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

    fun write(fileName: String, locations: List<LocationPointData>): File? {
        return try {
            val file = File(context.filesDir, fileName)
            Log.d("GPX", file.absolutePath)
            val writer = FileWriter(file).apply {
                append(header)
                append("<name>$fileName</name>\n")
                append(buildLocationsListXml(locations))
                append(footer)
            }
            writer.flush()
            writer.close()
            file
        } catch (ex: Exception) {
            Log.e(this.javaClass.name, ex.message.toString())
            null
        }
    }

    private fun buildLocationsListXml(locations: List<LocationPointData>): String {
        val sb = StringBuilder()
        for (location in locations) {
            sb.append(
                """
                    <trkpt lat="${location.latitude}" lon="${location.longitude}">
			            <ele>
				            ${location.altitude}
			            </ele>
			            <time>
				            ${dateFormat.format(Date(location.time))}
			            </time>
		            </trkpt>

                """.trimIndent()
            )
        }
        return sb.toString()
    }
}