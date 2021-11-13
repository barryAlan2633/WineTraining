package com.barryalan.winetraining.ui.menu.menu

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.with.MenuWithMenuItems
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuDetailViewModel(application: Application) : BaseViewModel(application) {

    val menuWithMenuItemsLiveData = MutableLiveData<MenuWithMenuItems>()

    fun fetch(recipeID: Long) {
        retrieveMenuWithMenuItemsFromDB(recipeID)
    }

    private fun retrieveMenuWithMenuItemsFromDB(menuID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val menuWithMenuItems =
                AppDatabase(getApplication()).menuDao().getMenuWithMenuItems(menuID)

            withContext(Dispatchers.Main) {
                menuWithMenuItemsLiveData.value = menuWithMenuItems
            }
        }
    }

    fun deleteMenuAndAssociations(menuID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).menuDao().deleteMenuWithRefs(menuID)
        }
    }
}
