package com.barryalan.winetraining.ui.menu.ingredients

import com.google.android.material.chip.Chip

interface IngredientCallback {
    fun onIngredientClicked(ingredientName: String)
}