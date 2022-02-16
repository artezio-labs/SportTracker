package com.artezio.sporttracker.domain.model

import androidx.room.*
import java.util.*

@Entity(
    tableName = "events",
)
data class Event(
    val name: String,
    val startDate: Long,
    var endDate: Long? = null,
    val sportsmanId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

data class EventWithData(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId",
        entity = LocationPointData::class
    )
    val locationDataList: List<LocationPointData>,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId",
        entity = PedometerData::class
    )
    val pedometerDataList: List<PedometerData>,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId",
        entity = TrackData::class
    )
    val trackDataList: List<TrackData>
)

data class EventWithLocations(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId",
        entity = LocationPointData::class
    )
    val locations: List<LocationPointData>
)

enum class EventStatus {
    PLANNED,
    ACTIVE,
    COMPLETED
}

