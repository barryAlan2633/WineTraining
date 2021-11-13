package com.barryalan.winetraining.model.employee.reference

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["employeeID", "jobName"])
data class EmployeeJobRef(
    @ColumnInfo(name = "employeeID")
    val employeeID: Long,

    @ColumnInfo(name = "jobName")
    val jobName: String
)
