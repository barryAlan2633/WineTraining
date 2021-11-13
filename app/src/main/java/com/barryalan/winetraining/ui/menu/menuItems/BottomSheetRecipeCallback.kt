package com.barryalan.winetraining.ui.menu.menuItems

import com.barryalan.winetraining.model.menu.Recipe

interface BottomSheetRecipeCallback {
    fun addRecipeWithAmount(recipe: Recipe, amount: String, unit: String)
    fun removeRecipeWithAmount(recipe: Recipe)
}