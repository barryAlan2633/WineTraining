package com.barryalan.winetraining.ui.menu.menu

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Menu
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MenuListViewModel(application: Application) : BaseViewModel(application) {

    val menusLiveData = MutableLiveData<List<Menu>>()
    val menusLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        retrieveMenusFromDB()
    }

    private fun retrieveMenusFromDB() {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val menus = AppDatabase(getApplication()).menuDao().getAllMenus()

            withContext(Dispatchers.Main) {
                menusRetrieved(menus)
            }
        }
    }

    private fun menusRetrieved(menuList: List<Menu>) {
        menusLiveData.value = menuList
        menusLoadError.value = false
        loading.value = false
    }

}