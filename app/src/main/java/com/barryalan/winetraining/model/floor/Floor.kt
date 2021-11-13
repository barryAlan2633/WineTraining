package com.barryalan.winetraining.model.floor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["floorName"], unique = true)])
data class Floor(

    @ColumnInfo(name = "floorName")
    var name: String,
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "floorID")
    var id: Long = 0L

}