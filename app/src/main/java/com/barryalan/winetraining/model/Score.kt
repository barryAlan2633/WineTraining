package com.barryalan.winetraining.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "table_score")
data class Score(
    @PrimaryKey
    var id: String = "",

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "score")
    var score: Int,

    @ColumnInfo(name = "type")
    var type: Int,

    @ColumnInfo(name="timeStamp")
    var timeStamp: String


) {



    constructor() : this("","test-player",0,  -1,"00-00-00T00:00:00.0000")
}


