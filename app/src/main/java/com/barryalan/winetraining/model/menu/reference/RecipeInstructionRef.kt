package com.barryalan.winetraining.model.menu.reference

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["recipeID", "instructionID"])
data class RecipeInstructionRef(

    @ColumnInfo(name = "recipeID")
    val recipeID: Long,

    @ColumnInfo(name = "instructionID")
    val instructionID: Long
)
