package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Price(

    @ColumnInfo(name = "price")
    val price:Float

){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "priceID")
    var id:Long = 0
}

