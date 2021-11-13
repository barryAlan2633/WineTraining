package com.barryalan.winetraining.ui.server

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.customer.Customer
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.with.FloorWithTablesAndSections
import com.barryalan.winetraining.model.menu.with.MenuWithMenuItems
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import com.barryalan.winetraining.util.StaticIntegers.Companion._ALL_SECTIONS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.barryalan.winetraining.util.StaticIntegers.Companion._SIT_ONE_MORE


class ServerMainViewModel(application: Application) : BaseViewModel(application) {
    val zoomLiveData = MutableLiveData(30)
    val floorWithTablesAndSectionsLiveData = MutableLiveData<FloorWithTablesAndSections>()
    val loggedServerIDLiveData = MutableLiveData(-1L)
    val customerLiveData = MutableLiveData<List<Customer>>(arrayListOf())
    val serversWithSectionLiveData = MutableLiveData<List<ServerWithSection>>()
    val serverStateLiveData = MutableLiveData(_SIT_ONE_MORE)
    val floorSettingsLiveData =
        MutableLiveData(R.id.chip_coworkers_section_bottom_sheet_server_floor_settings)
    val menusLiveData = MutableLiveData<List<MenuWithMenuItems>>()


    fun retrieveFloorFromDB(floorID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val floorWithTablesAndSections =
                AppDatabase(getApplication()).floorDao()
                    .getFloorWithTablesAndSections(floorID)

            withContext(Dispatchers.Main) {
                floorWithTablesAndSectionsLiveData.value = floorWithTablesAndSections
            }
        }
    }

    fun retrieveCustomersFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val customers =
                AppDatabase(getApplication()).customerDao().getAllCustomers()
            withContext(Dispatchers.Main) {
                customerLiveData.value = customers
            }
        }
    }

    fun retrieveServersWithSectionFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val serversWithSection =
                AppDatabase(getApplication()).employeeDao().getAllServersWithSections()
            withContext(Dispatchers.Main) {
                serversWithSectionLiveData.value = serversWithSection
            }
        }
    }

    fun retrieveMenusFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val menus =
                AppDatabase(getApplication()).appDao().getAllMenusWithMenuItems()
            withContext(Dispatchers.Main) {
                menusLiveData.value = menus
            }
        }
    }

    fun getTablesByID(tableIDs: List<Long>): List<Table> {
        val tables = mutableListOf<Table>()
        floorWithTablesAndSectionsLiveData.value?.allTables?.map { table ->
            tableIDs.map { tableID ->
                if (table.id == tableID) {
                    tables.add(table)
                }
            }
        }

        return tables
    }
}