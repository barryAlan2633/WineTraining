package com.barryalan.winetraining.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Amount(
    val amount: Float,
    val unit: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "amountID")
    var ID: Long = 0
}