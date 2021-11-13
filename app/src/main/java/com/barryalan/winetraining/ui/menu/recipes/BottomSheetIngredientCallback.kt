package com.barryalan.winetraining.ui.menu.recipes

import com.barryalan.winetraining.model.menu.Ingredient

interface BottomSheetIngredientCallback {
    fun addIngredientWithAmount(ingredient: Ingredient, amount: String, unit: String)
    fun removeIngredientWithAmount(ingredient: Ingredient)
}