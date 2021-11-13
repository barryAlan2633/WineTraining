package com.barryalan.winetraining.model.menu.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Instruction
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.reference.MenuItemInstructionRef
import com.barryalan.winetraining.model.menu.reference.MenuItemRecipeRef

data class MenuItemWithRecipes(

    @Embedded
    val menuItem: MenuItem,

    @Relation(
        parentColumn = "menuItemID",
        entity = Recipe::class,
        entityColumn = "recipeID",
        associateBy = Junction(
            value = MenuItemRecipeRef::class,
            parentColumn = "menuItemID",
            entityColumn = "recipeID"
        )
    )

    val recipes: List<Recipe>,

    @Relation(
        parentColumn = "menuItemID",
        entity = Amount::class,
        entityColumn = "amountID",
        associateBy = Junction(
            value = MenuItemRecipeRef::class,
            parentColumn = "menuItemID",
            entityColumn = "amountID"
        )
    )
    val amounts: List<Amount>,

    @Relation(
        parentColumn = "menuItemID",
        entity = Instruction::class,
        entityColumn = "instructionID",
        associateBy = Junction(
            value = MenuItemInstructionRef::class,
            parentColumn = "menuItemID",
            entityColumn = "instructionID"
        )
    )
    val instructions: List<Instruction>
)
