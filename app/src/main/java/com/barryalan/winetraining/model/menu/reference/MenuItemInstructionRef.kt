package com.barryalan.winetraining.model.menu.reference

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["menuItemID", "instructionID"])
data class MenuItemInstructionRef(

    @ColumnInfo(name = "menuItemID")
    val menuItemID: Long,

    @ColumnInfo(name = "instructionID")
    val instructionID: Long
)