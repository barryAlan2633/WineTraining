package com.barryalan.winetraining.model.employee.reference

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index


@Entity(
    indices = [
        Index(value = ["employeeID"], unique = true),
        Index(value = ["sectionID"], unique = true)
    ],
    primaryKeys = ["employeeID","sectionID","floorID"]
)
data class ServerSectionRef(
    @ColumnInfo(name = "floorID")
    val floorID: Long,

    @ColumnInfo(name = "employeeID")
    val employeeID: Long,

    @ColumnInfo(name = "sectionID")
    val sectionID: Long
)
