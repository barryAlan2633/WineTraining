package com.barryalan.winetraining.ui.manager.floor

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.floor.with.FloorWithTableTypes
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerFloorNewEditViewModel(application: Application) : BaseViewModel(application) {

    val chosenFloorLiveData = MutableLiveData<FloorWithTableTypes>()

    fun retrieveChosenFloorFromDB(floorID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val floor = AppDatabase(getApplication()).floorDao().getFloorWithTableType(floorID)

            withContext(Dispatchers.Main) {
                chosenFloorLiveData.value = floor
            }
        }
    }

    fun upsertChosenFloor(floorPlanWithTableTypes: FloorWithTableTypes) {
        viewModelScope.launch(Dispatchers.IO) {

            if (chosenFloorLiveData.value == null) {
                AppDatabase(getApplication()).floorDao()
                    .insertFloorWithTableType(floorPlanWithTableTypes)
            } else {
                AppDatabase(getApplication()).floorDao()
                    .updateFloorWithTableType(floorPlanWithTableTypes, chosenFloorLiveData.value!!)
            }


        }
    }


    fun deleteChosenFloor() {
        viewModelScope.launch(Dispatchers.IO) {
            chosenFloorLiveData.value?.let {
                AppDatabase(getApplication()).floorDao().deleteFloorAndAllRefs(
                    it.floor.id
                )
            }
        }
    }


}
