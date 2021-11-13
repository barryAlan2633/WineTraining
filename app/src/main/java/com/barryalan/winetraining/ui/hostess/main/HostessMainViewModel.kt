package com.barryalan.winetraining.ui.hostess.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.customer.Customer
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.with.FloorWithTablesAndSections
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HostessMainViewModel(application: Application) : BaseViewModel(application) {
    var zoomLiveData = MutableLiveData(30)
    val floorWithTablesAndSectionsLiveData = MutableLiveData<FloorWithTablesAndSections>()
    val customerLiveData = MutableLiveData<List<Customer>>(arrayListOf())
    val serversWithSectionLiveData = MutableLiveData<List<ServerWithSection>>()
    val serverIDToSitLiveData = MutableLiveData<Long>()

    fun refreshFloor() {
        retrieveFloorFromDB(floorWithTablesAndSectionsLiveData.value!!.floor.id)
    }

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

    fun saveNewCustomer(newCustomer: Customer) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).customerDao()
                .insertCustomer(newCustomer)

            retrieveFloorFromDB(floorWithTablesAndSectionsLiveData.value!!.floor.id)
        }
    }

    fun retrieveServerIDFromTableID(tableID: Long, numberOfServers: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val serverID = AppDatabase(getApplication()).appDao()
                .getTablesServerIDFromTableID(tableID, numberOfServers)

            withContext(Dispatchers.Main) {
                serverIDToSitLiveData.value = serverID
            }
        }
    }

    fun retrieveServerIDFromServerName(employeeName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val serverID = AppDatabase(getApplication()).appDao()
                .getEmployeeIDByName(employeeName)

            withContext(Dispatchers.Main) {
                serverIDToSitLiveData.value = serverID
            }
        }
    }
}