package com.barryalan.winetraining.model.floor.refrerence

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "FloorTableTypeRef", primaryKeys = ["floorID", "tableTypeID"])
data class FloorTableTypeRef(

    @ColumnInfo(name = "floorID")
    var floorID: Long,

    @ColumnInfo(name = "tableTypeID")
    var tableTypeID: Long
)

