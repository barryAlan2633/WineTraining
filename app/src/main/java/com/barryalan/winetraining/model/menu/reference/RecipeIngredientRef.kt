package com.barryalan.winetraining.model.menu.reference

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["recipeID", "ingredientID", "amountID"])
data class RecipeIngredientRef(

    @ColumnInfo(name = "recipeID")
    val recipeID: Long,

    @ColumnInfo(name = "ingredientID")
    val ingredientID: Long,

    @ColumnInfo(name = "amountID")
    val amountID: Long,
)
