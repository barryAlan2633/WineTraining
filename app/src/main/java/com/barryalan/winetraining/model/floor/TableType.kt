package com.barryalan.winetraining.model.floor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TableType")
data class TableType(

    @ColumnInfo(name = "height")
    var height: Int,

    @ColumnInfo(name = "length")
    var length: Int,

    @ColumnInfo(name = "rotation")
    var rotation: Float,

    @ColumnInfo(name = "booth_or_table")
    var boothOrTable: Int,

    @ColumnInfo(name = "max_number_of_seats")
    var maxNumberOfSeats: Int
){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tableTypeID")
    var id: Long = 0L
}
