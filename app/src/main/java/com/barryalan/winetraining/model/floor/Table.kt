package com.barryalan.winetraining.model.floor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Table")
data class Table(
    @ColumnInfo(name = "number")
    var number: Int,

    @ColumnInfo(name = "height")
    var height: Int,

    @ColumnInfo(name = "length")
    var length: Int,

    @ColumnInfo(name = "rotation")
    var rotation: Float,

    @ColumnInfo(name = "_X")
    var _X: Int?,

    @ColumnInfo(name = "_Y")
    var _Y: Int?,

    @ColumnInfo(name = "booth_or_table")
    var boothOrTable: Int,

    @ColumnInfo(name = "max_number_of_seats")
    var maxNumberOfSeats: Int,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tableID")
    var id: Long = 0L
}