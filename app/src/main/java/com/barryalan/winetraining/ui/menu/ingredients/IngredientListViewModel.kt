package com.barryalan.winetraining.ui.menu.ingredients

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.with.IngredientWithRecipes
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientListViewModel(application: Application) : BaseViewModel(application) {

    val ingredientWithRecipesListLiveData = MutableLiveData<List<IngredientWithRecipes>>()

    val searchSortByLiveData = MutableLiveData<Int>()



    val ingredientLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        retrieveIngredientsFromDB()
    }

    fun deleteIngredient(ingredientID: Long){
        deleteIngredientFromDB(ingredientID)
    }

    private fun retrieveIngredientsFromDB() {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val ingredients = AppDatabase(getApplication()).foodDao()
                .getAllIngredientWithRecipes()

            withContext(Dispatchers.Main) {
                ingredientsRetrieved(ingredients)
            }
        }
    }

    private fun ingredientsRetrieved(ingredientWithRecipesList: List<IngredientWithRecipes>) {
        ingredientWithRecipesListLiveData.value = ingredientWithRecipesList
        ingredientLoadError.value = false
        loading.value = false
    }

    private fun deleteIngredientFromDB(ingredientID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).foodDao().deleteIngredient(ingredientID)
        }
    }
}