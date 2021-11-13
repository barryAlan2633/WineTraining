package com.barryalan.winetraining.model.menu.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.Instruction
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.reference.RecipeIngredientRef
import com.barryalan.winetraining.model.menu.reference.RecipeInstructionRef

data class RecipeWithIngredients(

    @Embedded
    val recipe: Recipe,

    @Relation(
        parentColumn = "recipeID",
        entity = Ingredient::class,
        entityColumn = "ingredientID",
        associateBy = Junction(
            value = RecipeIngredientRef::class,
            parentColumn = "recipeID",
            entityColumn = "ingredientID"
        )
    )
    val ingredients: List<Ingredient>,

    @Relation(
        parentColumn = "recipeID",
        entity = Amount::class,
        entityColumn = "amountID",
        associateBy = Junction(
            value = RecipeIngredientRef::class,
            parentColumn = "recipeID",
            entityColumn = "amountID"
        )
    )
    val amounts: List<Amount>,

    @Relation(
        parentColumn = "recipeID",
        entity = Instruction::class,
        entityColumn = "instructionID",
        associateBy = Junction(
            value = RecipeInstructionRef::class,
            parentColumn = "recipeID",
            entityColumn = "instructionID"
        )
    )
    val instructions: List<Instruction>
)
