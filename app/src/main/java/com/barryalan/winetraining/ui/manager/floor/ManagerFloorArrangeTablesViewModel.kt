package com.barryalan.winetraining.ui.manager.floor

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.with.FloorWithTablesTableTypes
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import com.barryalan.winetraining.util.StaticIntegers.Companion.MOVING_OFF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerFloorArrangeTablesViewModel(application: Application) : BaseViewModel(application) {
    val chosenFloorLiveData = MutableLiveData<FloorWithTablesTableTypes>()
    val selectedTablesLiveData = MutableLiveData<MutableList<Table>>(arrayListOf())
    val movingModeLiveData = MutableLiveData(MOVING_OFF)
    var zoomLiveData = MutableLiveData(30)


    fun refresh() {
        retrieveChosenFloorFromDB(chosenFloorLiveData.value!!.floor.id)
    }

    fun retrieveChosenFloorFromDB(floorID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val floor =
                AppDatabase(getApplication()).floorDao().getFloorWithTablesTableTypes(floorID)

            withContext(Main) {
                chosenFloorLiveData.value = floor
            }
        }
    }

    fun saveTableAndRefresh(table: Table) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).floorDao()
                .insertTableWithRef(chosenFloorLiveData.value!!.floor.id, table)
            refresh()
        }
    }

    fun updateTableAndRefresh(selectedTable: Table) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).floorDao().updateTable(selectedTable)
            refresh()
        }

    }

    fun deleteSelectedTablesAndRefsAndRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedTablesLiveData.value!!.map {
                AppDatabase(getApplication()).floorDao()
                    .deleteTableWithRef(it.id)
            }
            withContext(Main) {
                selectedTablesLiveData.value = arrayListOf()
            }

            refresh()
        }
    }

    fun duplicateSelectedTablesAndRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedTablesLiveData.value!!.map { selectedTable ->

                val tableDuplicate = Table(
                    selectedTable.number,
                    selectedTable.height,
                    selectedTable.length,
                    selectedTable.rotation,
                    selectedTable._X,
                    selectedTable._Y,
                    selectedTable.boothOrTable,
                    selectedTable.maxNumberOfSeats
                )

                AppDatabase(getApplication()).floorDao()
                    .insertTableWithRef(chosenFloorLiveData.value!!.floor.id, tableDuplicate)
            }

            refresh()
        }
    }

    fun updateSelectedTables() {
        selectedTablesLiveData.value!!.map {
            updateTableAndRefresh(it)
        }
    }

    fun initGloriaFloor() {

        arrayListOf(
            Table(
                number = 1,
                height = 50,
                length = 50,
                rotation = 45f,
                _X = 1240,
                _Y = 900,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 2,
                height = 50,
                length = 50,
                rotation = 45f,
                _X = 1540,
                _Y = 900,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 3,
                height = 50,
                length = 50,
                rotation = 45f,
                _X = 1940,
                _Y = 900,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 4,
                height = 50,
                length = 50,
                rotation = 45f,
                _X = 2540,
                _Y = 900,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 5,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2880,
                _Y = 920,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 6,
                height = 40,
                length = 60,
                rotation = 150f,
                _X = 780,
                _Y = 1330,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 8,
                height = 40,
                length = 60,
                rotation = 110f,
                _X = 650,
                _Y = 1500,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 10,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 1290,
                _Y = 1330,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 11,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1590,
                _Y = 1330,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 12,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 1890,
                _Y = 1330,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 20,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 1040,
                _Y = 1570,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 21,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1340,
                _Y = 1570,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 22,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 1640,
                _Y = 1570,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 23,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 1940,
                _Y = 1570,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 30,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1040,
                _Y = 1810,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 31,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1340,
                _Y = 1810,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 32,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1640,
                _Y = 1810,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 33,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 1940,
                _Y = 1810,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

//==========================================
            Table(
                number = 13,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2290,
                _Y = 1330,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 14,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2590,
                _Y = 1330,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 15,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 2950,
                _Y = 1330,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 24,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2290,
                _Y = 1540,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 25,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2590,
                _Y = 1540,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 26,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 2950,
                _Y = 1540,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 34,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2290,
                _Y = 1780,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 35,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2590,
                _Y = 1780,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 36,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 2950,
                _Y = 1780,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            //=======================


            Table(
                number = 40,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2290,
                _Y = 2080,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 41,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2590,
                _Y = 2080,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 42,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 2890,
                _Y = 2080,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 43,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 3290,
                _Y = 2080,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),

            //====================================
            Table(
                number = 44,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3340,
                _Y = 1860,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 45,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3340,
                _Y = 1630,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 46,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3340,
                _Y = 1400,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 47,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3340,
                _Y = 1170,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 48,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3340,
                _Y = 940,
                boothOrTable = 0,
                maxNumberOfSeats = 5,

                ),

            //==================

            Table(
                number = 50,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 50,
                _Y = 1850,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 51,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 400,
                _Y = 1970,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 52,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 50,
                _Y = 2090,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 53,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 400,
                _Y = 2210,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            //===============================
            Table(
                number = 60,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 650,
                _Y = 2510,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 61,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 650,
                _Y = 2740,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 62,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 650,
                _Y = 2980,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 63,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 350,
                _Y = 2870,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 64,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 50,
                _Y = 2980,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 65,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 50,
                _Y = 2740,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 66,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 350,
                _Y = 2630,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 67,
                height = 40,
                length = 60,
                rotation = 0f,
                _X = 50,
                _Y = 2510,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            //======================================================================================= PATIO
            Table(
                number = 90,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 1000,
                _Y = 700,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 91,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1340,
                _Y = 700,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 92,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1640,
                _Y = 700,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 93,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 1940,
                _Y = 700,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 94,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2540,
                _Y = 700,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 95,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2840,
                _Y = 700,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 96,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3140,
                _Y = 700,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 80,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2240,
                _Y = 460,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 81,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2540,
                _Y = 460,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 82,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2840,
                _Y = 460,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 83,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3140,
                _Y = 460,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 84,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3440,
                _Y = 460,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 70,
                height = 40,
                length = 60,
                rotation = 66f,
                _X = 1640,
                _Y = 380,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 71,
                height = 40,
                length = 60,
                rotation = 66f,
                _X = 1940,
                _Y = 300,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 72,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2240,
                _Y = 220,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 73,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2540,
                _Y = 220,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),

            Table(
                number = 74,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 2840,
                _Y = 220,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 75,
                height = 40,
                length = 40,
                rotation = 0f,
                _X = 3165,
                _Y = 190,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
            Table(
                number = 76,
                height = 40,
                length = 60,
                rotation = 90f,
                _X = 3440,
                _Y = 220,
                boothOrTable = 1,
                maxNumberOfSeats = 5,

                ),
        ).map {
            saveTableAndRefresh(it)
        }






    }

}