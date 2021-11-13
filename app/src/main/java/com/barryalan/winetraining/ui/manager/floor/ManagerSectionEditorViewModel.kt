package com.barryalan.winetraining.ui.manager.floor

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.floor.Section
import com.barryalan.winetraining.model.floor.SectionColor
import com.barryalan.winetraining.model.floor.refrerence.SectionTableRef
import com.barryalan.winetraining.model.floor.with.FloorWithTablesAndSections
import com.barryalan.winetraining.model.floor.with.SectionWithTables
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import com.barryalan.winetraining.util.forceRefresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerSectionEditorViewModel(application: Application) : BaseViewModel(application) {

    val selectedNumberOfSectionsLiveData = MutableLiveData(0)
    val floorWithTablesAndSectionsLiveData = MutableLiveData<FloorWithTablesAndSections>()
    val sectionColorsLiveData = MutableLiveData<List<SectionColor>>()
    val serversLiveData = MutableLiveData<List<Employee>>()
    val relevantSectionsLiveData = MutableLiveData<List<SectionWithTables>>()
    val selectedSectionIdLiveData = MutableLiveData(-1L)
    var zoomLiveData = MutableLiveData(30)


    private fun refreshFloor() {
        retrieveFloorFromDB(floorWithTablesAndSectionsLiveData.value!!.floor.id)

    }

    fun retrieveFloorFromDB(floorID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val floorWithTablesAndSections =
                AppDatabase(getApplication()).floorDao()
                    .getFloorWithTablesAndSections(floorID)

            withContext(Dispatchers.Main) {
                floorWithTablesAndSectionsLiveData.value = floorWithTablesAndSections
                selectedNumberOfSectionsLiveData.forceRefresh()
            }
        }
    }

    fun retrieveServersFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val servers =
                AppDatabase(getApplication()).employeeDao().getAllServers()

            withContext(Dispatchers.Main) {
                serversLiveData.value = servers
            }
        }
    }

    fun retrieveSectionColorsFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val sectionColors =
                AppDatabase(getApplication()).floorDao().getAllSectionColors()

            withContext(Dispatchers.Main) {
                sectionColorsLiveData.value = sectionColors
                selectedNumberOfSectionsLiveData.forceRefresh()
            }
        }
    }

    fun getRelevantSections(selectedNumberOfSections: Int) {

        val relevantSections = mutableListOf<SectionWithTables>()

        floorWithTablesAndSectionsLiveData.value?.allSections?.map {
            if (it.section.numberOfServers == selectedNumberOfSections) {
                relevantSections.add(it)
            }
        }

        relevantSections.sortBy { r -> r.section.number }

        relevantSectionsLiveData.value = relevantSections
    }

    fun saveAllSectionColors(sectionColors: List<SectionColor>) {
        viewModelScope.launch(Dispatchers.IO) {
            sectionColors.map {
                AppDatabase(getApplication()).floorDao().saveSectionColor(it)
            }

            retrieveSectionColorsFromDB()
        }
    }

    fun saveSection(section: Section) {
        viewModelScope.launch(Dispatchers.IO) {

            floorWithTablesAndSectionsLiveData.value?.let {
                AppDatabase(getApplication()).floorDao().insertSectionWithRef(
                    section,
                    it.floor.id
                )
                refreshFloor()
            }

        }
    }

    fun saveOrDeleteSectionTableRef(tableID: Long, selectedSectionID: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            floorWithTablesAndSectionsLiveData.value?.let {

                floorWithTablesAndSectionsLiveData.value?.let {
                    AppDatabase(getApplication()).floorDao().insertSectionTableRefAndDeleteOthers(
                        SectionTableRef(
                            selectedSectionID,
                            tableID,
                            selectedNumberOfSectionsLiveData.value!!
                        ),
                        selectedNumberOfSectionsLiveData.value!!
                    )
                }

                refreshFloor()
            }
        }
    }

    fun initAllSections(sections: MutableList<Section>) {
        viewModelScope.launch(Dispatchers.IO) {

            floorWithTablesAndSectionsLiveData.value?.let { floor ->

                sections.map { section ->

                    AppDatabase(getApplication()).floorDao().insertSectionWithRef(
                        section,
                        floor.floor.id
                    )
                }
                refreshFloor()
            }

        }
    }


}