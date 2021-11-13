package com.barryalan.winetraining.ui.menu.recipes

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.with.RecipeWithIngredients
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class RecipeListViewModel(application: Application) : BaseViewModel(application) {

    private val recipesLiveData = MutableLiveData<MutableList<RecipeWithIngredients>>()
    val ingredientsLiveData = MutableLiveData<List<Ingredient>>()

    val recipesFilteredLiveData = MutableLiveData<MutableList<Recipe>>()
    val filterLiveData = MutableLiveData<MutableList<String>>(mutableListOf())
    val searchSortByLiveData = MutableLiveData(-1)

    val recipeLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()


    fun refresh() {
        retrieveRecipesFromDB()
        retrieveIngredientsFromDB()
    }

    private fun retrieveRecipesFromDB() {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = AppDatabase(getApplication()).foodDao().getAllRecipesWithIngredients()

            withContext(Dispatchers.Main) {
                recipesRetrieved(recipes)
            }
        }
    }

    private fun retrieveIngredientsFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val ingredients = AppDatabase(getApplication()).foodDao().getAllIngredients()

            withContext(Dispatchers.Main) {
                ingredientsLiveData.value = ingredients
            }
        }
    }

    private fun recipesRetrieved(recipeWithIngredientsList: MutableList<RecipeWithIngredients>) {
        recipesLiveData.value = recipeWithIngredientsList
        filterRecipes(filterLiveData.value)
        recipeLoadError.value = false
        loading.value = false
    }


    private fun filterRecipes(filterList: List<String>?) {

        //todo learn about binary search and maybe update this shit
        var filteredRecipeList = mutableListOf<Recipe>()
        val recipesToRemove = mutableListOf<Recipe>()


        if (filterList.isNullOrEmpty()) {
            if (recipesLiveData.value != null) {
                filteredRecipeList = recipesLiveData.value!!.map { it.recipe } as MutableList
            } else {
                filteredRecipeList = arrayListOf()
            }
        } else {

            recipesLiveData.value?.let { recipeWithIngredients ->
                recipeWithIngredients.map { recipe ->
                    filterList.map { filter ->

                        if (filter.contains("not") && recipe.ingredients.isEmpty()) {
                            filteredRecipeList.add(recipe.recipe)
                        }

                        recipe.ingredients.map { ingredient ->

                            if (ingredient.name == filter.removePrefix("not").trim()
                                    .toLowerCase(Locale.ROOT)
                            ) {
                                if (filter.contains("not")) {
                                    recipesToRemove.add(recipe.recipe)
                                } else {
                                    filteredRecipeList.add(recipe.recipe)
                                }
                            } else if (filter.contains("not")) {
                                filteredRecipeList.add(recipe.recipe)
                            } else {
                            }

                        }
                    }
                }
            }
        }


        recipesFilteredLiveData.value =
            filteredRecipeList.distinct()
                .filter { !recipesToRemove.contains(it) } as MutableList<Recipe>

    }

}