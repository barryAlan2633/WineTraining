package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Recipe(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "type")
    val type: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipeID")
    var id: Long = 0

    @ColumnInfo(name = "calories")
    var calories: Int? = null
}
