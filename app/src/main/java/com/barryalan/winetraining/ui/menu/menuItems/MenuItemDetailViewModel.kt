package com.barryalan.winetraining.ui.menu.menuItems

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.with.MenuItemWithRecipes
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuItemDetailViewModel(application: Application) : BaseViewModel(application) {

    val menuItemWithRecipesLiveData = MutableLiveData<MenuItemWithRecipes>()

    fun fetch(recipeID: Long) {
        retrieveMenuItemWithRecipesFromDB(recipeID)
    }

    private fun retrieveMenuItemWithRecipesFromDB(menuItemID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val menuItemWithRecipes =
                AppDatabase(getApplication()).menuDao().getMenuItemWithRecipes(menuItemID)

            withContext(Dispatchers.Main) {
                menuItemWithRecipesLiveData.value = menuItemWithRecipes
            }
        }
    }

    fun deleteMenuItemAndAssociations(menuItemID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).menuDao().deleteMenuItemWithRefs(menuItemID)
        }
    }
}