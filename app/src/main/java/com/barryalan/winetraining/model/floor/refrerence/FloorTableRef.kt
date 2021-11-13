package com.barryalan.winetraining.model.floor.refrerence

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "FloorTableRef", primaryKeys = ["floorID", "tableID"])
data class FloorTableRef(

    @ColumnInfo(name = "floorID")
    var floorID: Long,
    @ColumnInfo(name = "tableID")
    var tableID: Long
)