package com.barryalan.winetraining.ui.manager.floor

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.floor.Floor
import com.barryalan.winetraining.model.floor.with.FloorWithTableTypes
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerFloorListViewModel(application: Application) : BaseViewModel(application) {
    val floorsLiveData = MutableLiveData<List<Floor>>()

    fun refresh(){
        retrieveALlFloors()
    }

    private fun retrieveALlFloors(){
        viewModelScope.launch(Dispatchers.IO) {
            val floors = AppDatabase(getApplication()).floorDao().getAllFloors()

            withContext(Dispatchers.Main) {
                floorsLiveData.value = floors
            }
        }
    }

}