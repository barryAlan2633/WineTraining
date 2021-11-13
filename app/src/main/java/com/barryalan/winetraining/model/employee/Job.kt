package com.barryalan.winetraining.model.employee

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Job(
    @PrimaryKey
    @ColumnInfo(name = "name")
    var name: String
)
