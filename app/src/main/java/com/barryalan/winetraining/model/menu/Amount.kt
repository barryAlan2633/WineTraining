package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Amount(

    @ColumnInfo(name="amount")
    val amount: Float,

    @ColumnInfo(name="unit")
    val unit: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "amountID")
    var id: Long = 0
}