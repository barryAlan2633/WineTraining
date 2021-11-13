package com.barryalan.winetraining.model.floor.refrerence

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "FloorSectionRef", primaryKeys = ["floorID", "sectionID"])
data class FloorSectionRef(
    @ColumnInfo(name = "floorID")
    var floorID: Long,

    @ColumnInfo(name = "sectionID")
    var sectionID: Long
)