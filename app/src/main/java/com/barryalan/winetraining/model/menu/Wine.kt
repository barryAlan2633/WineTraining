package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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