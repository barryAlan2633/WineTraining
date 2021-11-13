package com.barryalan.winetraining.ui.menu.menu

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Menu
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.Price
import com.barryalan.winetraining.model.menu.with.MenuWithMenuItems
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.*

class MenuNewEditViewModel(application: Application) : BaseViewModel(application) {

    val menuToUpdateLiveData = MutableLiveData<MenuWithMenuItems>()
    val menuItemListLiveData = MutableLiveData<List<MenuItem>>()
    val menuItemsToAddLiveData = MutableLiveData<MutableList<MenuItem>>()

    fun saveMenuWithMenuItems(
        menu: Menu,
        menuItems: ArrayList<MenuItem>,
        menuItemsPrices: ArrayList<Price>,

        ): Job {
        return viewModelScope.launch {
            AppDatabase(getApplication()).menuDao().insertMenuWithMenuItems(
                menu,
                menuItems,
                menuItemsPrices
            )
        }
    }

    fun fetchSelectedMenuWithMenuItems(menuItemID: Long) {
        retrieveMenuItemWithRecipesFromDB(menuItemID)
    }

    fun fetchMenuItemList() {
        retrieveMenuItemListFromDB()
    }

    private fun retrieveMenuItemWithRecipesFromDB(menuID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val menuItemToUpdate =
                AppDatabase(getApplication()).menuDao().getMenuWithMenuItems(menuID)

            withContext(Dispatchers.Main) {
                menuToUpdateLiveData.value = menuItemToUpdate
            }
        }
    }

    fun updateMenuWithMenuItems(
        updatedMenu: Menu,
        updatedMenuItems: ArrayList<MenuItem>,
        menuItemsPrices: ArrayList<Price>,
    ): Job {
        return viewModelScope.launch(Dispatchers.IO) {

            //make sure that the recipeToUpdate has been fetched because we need its fields
            menuToUpdateLiveData.value?.let { menuWithMenuItemsToUpdate ->

                viewModelScope.launch(Dispatchers.IO) {
                    //update recipeWithIngredients object where it differs from the previous instance
                    AppDatabase(getApplication()).menuDao().updateMenuWithMenuItems(
                        updatedMenu,
                        updatedMenuItems,
                        menuWithMenuItemsToUpdate,
                        menuItemsPrices
                    )
                }

            }
            if (menuToUpdateLiveData.value == null) {
                Log.d("Error:", "menuToUpdate has not been retrieved")
            }
        }
    }

    fun initGloriasMenus() {
        val gloriasMenus = arrayListOf(
            Menu("Appetizers", null, "food"),
            Menu("Lunch", null, "food"),
            Menu("Dinner", null, "food"),
            Menu("Brunch", null, "food"),
            Menu("Desserts", null, "food"),
            Menu("Bar", null, "food"),
            Menu("Valentine's Day", null, "food"),
            Menu("Mother's Day", null, "food")
        )

        gloriasMenus.map {
            saveMenuWithMenuItems(
                it,
                arrayListOf(),
                arrayListOf()
            )
        }
    }


    fun nukeMenus() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).menuDao().nukeMenuTable()
        }
    }

    private fun retrieveMenuItemListFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val menuItemList = AppDatabase(getApplication()).menuDao().getAllMenuItems()

            withContext(Dispatchers.Main) {
                menuItemListLiveData.value = menuItemList
            }
        }
    }

    fun deleteMenuItemWithPrices(menuItemID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            menuToUpdateLiveData.value?.menu?.let {
                AppDatabase(getApplication()).menuDao().deleteMenuItemWithPrices(menuItemID,
                    it.id)
            }
        }
    }
}
