package com.artezio.osport.tracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.artezio.osport.tracker.util.MINUTE_IN_MILLIS
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

    fun getDurationPeriod(): LongRange {
        return (this.startDate..this.duration * MINUTE_IN_MILLIS)
    }

    fun hasIntersection(startTime: Long, duration: Long): Boolean {
        val thisEndDate = this.startDate + this.duration
        val otherEndDate = startTime + duration
        return (between(this.startDate, startTime, otherEndDate) || between(thisEndDate, startTime, otherEndDate))
                || (between(startTime, this.startDate, thisEndDate) || between(thisEndDate, this.startDate, thisEndDate))
    }

    override fun toString(): String {
        return "PlannedEvent(id='$id', name='$name', startDate=$startDate, duration=$duration)"
    }
    // 14:00 3 15:00 4
}