package com.artezio.osport.tracker.domain.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "events",
)
data class Event(
    val name: String,
    val startDate: Long,
    var endDate: Long? = null,
    val sportsmanId: Long,
    val timerValue: Double = 0.0,
    val speedValue: Double = 0.0,
    val distanceValue: Double = 0.0,
    val tempoValue: Double = 0.0,
    val stepsValue: Int = 0,
    val gpsPointsValue: Int = 0
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

