package com.barryalan.winetraining.model.menu.reference

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["menuID", "menuItemID", "priceID"])
data class MenuMenuItemRef(

    @ColumnInfo(name = "menuID")
    val menuID: Long,

    @ColumnInfo(name = "menuItemID")
    val menuItemID: Long,

    @ColumnInfo(name = "priceID")
    val priceID: Long
)