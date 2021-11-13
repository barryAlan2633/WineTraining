package com.barryalan.winetraining.model.menu.reference

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["menuItemID", "recipeID", "amountID"])
data class MenuItemRecipeRef(

    @ColumnInfo(name = "menuItemID")
    val menuItemID: Long,

    @ColumnInfo(name = "recipeID")
    val recipeID: Long,

    @ColumnInfo(name = "amountID")
    val amountID: Long
)