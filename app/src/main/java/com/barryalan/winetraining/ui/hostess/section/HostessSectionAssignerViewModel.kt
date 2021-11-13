package com.barryalan.winetraining.ui.hostess.section

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.reference.ServerSectionRef
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.SectionColor
import com.barryalan.winetraining.model.floor.with.FloorWithTablesAndSections
import com.barryalan.winetraining.model.floor.with.SectionWithTables
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HostessSectionAssignerViewModel(application: Application) : BaseViewModel(application) {
    var zoomLiveData = MutableLiveData(30)
    val floorWithTablesAndSectionsLiveData = MutableLiveData<FloorWithTablesAndSections>()
    val sectionColorsLiveData = MutableLiveData<List<SectionColor>>()
    val serversWithSectionLiveData = MutableLiveData<List<ServerWithSection>>()
    val relevantSectionsLiveData = MutableLiveData<List<SectionWithTables>>()
    val checkedServers = MutableLiveData<MutableList<ServerWithSection>>(arrayListOf())

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

    fun retrieveServersFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val servers =
                AppDatabase(getApplication()).employeeDao().getAllServersWithSections()

            withContext(Dispatchers.Main) {
                serversWithSectionLiveData.value = servers
            }

        }
    }

    fun retrieveSectionColorsFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val sectionColors =
                AppDatabase(getApplication()).floorDao().getAllSectionColors()

            withContext(Dispatchers.Main) {
                sectionColorsLiveData.value = sectionColors
            }
        }
    }

    fun assignSections(serversToAssign: List<ServerWithSection>) {

        val relevantSections = mutableListOf<SectionWithTables>()

        if (floorWithTablesAndSectionsLiveData.value != null) {


            floorWithTablesAndSectionsLiveData.value!!.allSections.map {
                if (it.section.numberOfServers == serversToAssign.size) {
                    relevantSections.add(it)
                }

            }

            saveAllServerSectionRefsDistinct(
                relevantSections.shuffled(),
                serversWithSectionLiveData.value,
                serversToAssign
            )

        }
    }


    private fun saveAllServerSectionRefsDistinct(
        relevantSections: List<SectionWithTables>,
        allServers: List<ServerWithSection>?,
        serversToAssign: List<ServerWithSection>
    ) {
        viewModelScope.launch(Dispatchers.IO) {


            AppDatabase(getApplication()).floorDao()
                .deleteAllFloorServerSectionRefs(floorWithTablesAndSectionsLiveData.value!!.floor.id)

            relevantSections.mapIndexed { index, section ->

                AppDatabase(getApplication()).floorDao().insertServerSectionRef(
                    ServerSectionRef(
                        floorWithTablesAndSectionsLiveData.value!!.floor.id,
                        serversToAssign[index].employee.id,
                        section.section.id
                    )
                )

            }


            retrieveServersFromDB()


        }
    }


}




