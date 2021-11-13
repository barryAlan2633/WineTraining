package com.barryalan.winetraining.model.floor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SectionColor")
data class SectionColor(

    @PrimaryKey
    @ColumnInfo(name = "sectionNumber")
    val number:Int,

    @ColumnInfo(name = "sectionColor")
    val color:Int
)
