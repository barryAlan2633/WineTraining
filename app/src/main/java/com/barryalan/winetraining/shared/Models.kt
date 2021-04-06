package com.barryalan.winetraining.shared

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
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

@Entity(tableName = "table_score")
data class Score(
    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    override fun toString(): String {
        return "Score{" +
                "score=" + score +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}'
    }
}

@Entity(tableName = "table_wine")
data class Wine(
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "glassPrice")
    val glassPrice: Float,

    @ColumnInfo(name = "bottlePrice")
    val bottlePrice: Float
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}
