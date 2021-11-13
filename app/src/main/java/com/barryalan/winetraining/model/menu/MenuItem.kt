package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value = ["name"], unique = true)])
data class MenuItem(

    @ColumnInfo(name="name")
    var name: String,

    @ColumnInfo(name="image")
    val image: String?,

    @ColumnInfo(name="type")
    val type: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "menuItemID")
    var id: Long = 0L //this has to be 0 don't make it into -1

    @ColumnInfo(name = "calories")
    var calories: Int? = null
}