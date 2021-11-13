package com.barryalan.winetraining.ui.menu.recipes

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.with.RecipeWithIngredients
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailViewModel(application: Application) : BaseViewModel(application) {

    val recipeWithIngredientsLiveData = MutableLiveData<RecipeWithIngredients>()

    fun fetch(recipeID: Long) {
        retrieveRecipeWithIngredientsFromDB(recipeID)
    }

    private fun retrieveRecipeWithIngredientsFromDB(recipeID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipeWithIngredientsDetails =
                AppDatabase(getApplication()).foodDao().getRecipeWithIngredients(recipeID)

            withContext(Dispatchers.Main) {
                recipeWithIngredientsLiveData.value = recipeWithIngredientsDetails
            }
        }
    }

    fun deleteRecipeWithIngredients(recipeID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).foodDao().deleteRecipeAndAssociations(recipeID)
        }
    }
}