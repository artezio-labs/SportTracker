package com.artezio.osport.tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.artezio.osport.tracker.util.MINUTE_IN_MILLIS
import com.artezio.osport.tracker.util.SECOND_IN_MILLIS
import com.artezio.osport.tracker.util.between

@Entity(tableName = "planned_events")
data class PlannedEvent(
    val name: String,
    val startDate: Long,
    val duration: Int = 120,
    val calibrationTime: Int = 1
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    fun hasIntersection(startDate: Long, duration: Int): Boolean {
        val thisStartDate = this.startDate - this.calibrationTime * SECOND_IN_MILLIS
        val endDate = startDate + duration * MINUTE_IN_MILLIS
        val thisEndDate = this.startDate + this.duration * MINUTE_IN_MILLIS
        return (startDate.between(thisStartDate, thisEndDate) || endDate.between(thisStartDate, thisEndDate)) ||
                (thisStartDate.between(startDate, endDate) || thisEndDate.between(startDate, endDate))
    }


    override fun toString(): String {
        return "PlannedEvent(id='$id', name='$name', startDate=$startDate, duration=$duration)"
    }
}