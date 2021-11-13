package com.barryalan.winetraining.model.menu.with


import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.reference.RecipeIngredientRef

data class IngredientWithRecipes(

    @Embedded
    val ingredient: Ingredient,

    @Relation(
        parentColumn = "ingredientID",
        entity = Recipe::class,
        entityColumn = "recipeID",
        associateBy = Junction(
            value = RecipeIngredientRef::class,
            parentColumn = "ingredientID",
            entityColumn = "recipeID"
        )
    )
    val recipes: List<Recipe>
)
