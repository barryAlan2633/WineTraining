package com.barryalan.winetraining.model.floor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//SECTIONS
@Entity(tableName = "Section", indices = [Index(value = ["number","numberOfServer"], unique = true)])
data class Section(
    @ColumnInfo(name = "number")
    var number: Int,

    @ColumnInfo(name = "numberOfServer")
    var numberOfServers: Int
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sectionID")
    var id: Long = 0
}