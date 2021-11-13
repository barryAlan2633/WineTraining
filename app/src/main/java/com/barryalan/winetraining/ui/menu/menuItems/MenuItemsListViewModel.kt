package com.barryalan.winetraining.ui.menu.menuItems

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.with.MenuItemWithRecipes
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MenuItemsListViewModel(application: Application) : BaseViewModel(application) {

    val menuItemsLiveData = MutableLiveData<List<MenuItemWithRecipes>>()
    val ingredientsLiveData = MutableLiveData<List<Ingredient>>()

    val menuItemsFilteredLiveData = MutableLiveData<MutableList<MenuItem>>()
    val filterLiveData = MutableLiveData<MutableList<String>>(mutableListOf())
    val searchSortByLiveData = MutableLiveData<Int>(-1)


    val menuItemsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        retrieveMenuItemsFromDB()
        retrieveIngredientsFromDB()

    }

    private fun retrieveMenuItemsFromDB() {
        loading.value = true
        viewModelScope.launch(IO) {
            val menuItemsWithRecipes =
                AppDatabase(getApplication()).menuDao().getAllMenuItemWithRecipes()

            withContext(Main) {
                menuItemsRetrieved(menuItemsWithRecipes)
            }
        }
    }

    private fun retrieveIngredientsFromDB() {
        viewModelScope.launch(IO) {
            val ingredients = AppDatabase(getApplication()).foodDao().getAllIngredients()

            withContext(Main) {
                ingredientsLiveData.value = ingredients
            }
        }
    }


    private fun menuItemsRetrieved(menuItemsWithRecipesList: List<MenuItemWithRecipes>) {
        menuItemsLiveData.value = menuItemsWithRecipesList
        filterMenuItems(filterLiveData.value, ingredientsLiveData.value?.map{it.id})
        menuItemsLoadError.value = false
        loading.value = false
    }

    private fun filterMenuItems(filterList: List<String>?, allIngredientIDs: List<Long>?) {

        //todo learn about binary search and maybe update this shit
        var filteredMenuItemList = mutableListOf<MenuItem>()


        if (filterList.isNullOrEmpty()) {

            if (menuItemsLiveData.value != null) {
                filteredMenuItemList =
                    menuItemsLiveData.value!!.map { it.menuItem } as MutableList<MenuItem>
            } else {
                filteredMenuItemList = arrayListOf()
            }

            menuItemsFilteredLiveData.value = filteredMenuItemList
        } else {

            menuItemsLiveData.value?.let { menuItemWithRecipesList ->

                viewModelScope.launch(IO) {
                    val recipeIds =
                        withContext(IO) {
                            AppDatabase(getApplication()).menuDao()
                                .getRecipeIDFromIngredientRefContainingIngredient(filterList,allIngredientIDs)
                        }

                    withContext(IO) {
                        menuItemWithRecipesList.map { menuItemWithRecipes ->
                            menuItemWithRecipes.recipes.map { recipe ->
                                recipeIds.map { recipeID ->
                                    if (recipeID == recipe.id) {
                                        filteredMenuItemList.add(menuItemWithRecipes.menuItem)
                                    }
                                }
                            }
                        }
                    }

                    withContext(Main) {
                        menuItemsFilteredLiveData.value = filteredMenuItemList
                    }

                }
            }
        }
    }
}