package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class Ingredient(

    @ColumnInfo(name="name")
    var name: String,

    @ColumnInfo(name="image")
    val image: String?,

    @ColumnInfo(name="calories")
    val calories: Int?

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredientID")
    var id: Long = 0
}
