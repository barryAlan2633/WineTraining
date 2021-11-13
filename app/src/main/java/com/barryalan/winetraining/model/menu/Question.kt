package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_question")
data class Question(

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "question")
    val question: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}