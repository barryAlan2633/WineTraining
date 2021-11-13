package com.barryalan.winetraining.model.employee

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["clockInID"], unique = true)])
data class Employee(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "employeeID")
    var id: Long = 0,

    @ColumnInfo(name = "clockInID")
    val clockInID: Long = id,

    @ColumnInfo(name = "name")
    val name: String,
)
