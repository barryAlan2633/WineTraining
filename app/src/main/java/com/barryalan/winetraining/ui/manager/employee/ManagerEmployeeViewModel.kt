package com.barryalan.winetraining.ui.manager.employee

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.Job
import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerEmployeeViewModel(application: Application) : BaseViewModel(application) {


    val searchSortByLiveData = MutableLiveData(-1)
    val filterLiveData = MutableLiveData<String>(null)
    val employeesLiveData = MutableLiveData<List<EmployeeWithJobs>>()
    val jobsLiveData = MutableLiveData(
        listOf(
            Job("manager"),
            Job("server"),
            Job("hostess"),
            Job("cook"),
            Job("busser"),
            Job("dishwasher"),
            Job("foodrunner"),
            Job("bartender")
        )

    //make ints of these and import instead
    )

    fun refresh() {
        initJobs()
        retrieveEmployeesFromDB()
    }

    private fun initJobs() {


        viewModelScope.launch(Dispatchers.IO) {
            jobsLiveData.value?.let {
                AppDatabase(getApplication()).employeeDao().insertAllJobs(
                    it
                )
            }
        }
    }

    private fun retrieveEmployeesFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val employeesWithJobs =
                AppDatabase(getApplication()).employeeDao().getAllEmployeeWithJobs()

            withContext(Dispatchers.Main) {
                employeesLiveData.value = employeesWithJobs
            }
        }
    }

    fun createNewEmployee(newEmployee: EmployeeWithJobs) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).employeeDao().insertEmployeeWithRefs(newEmployee)
            refresh()
        }
    }

    fun updateEmployee(newEmployee: EmployeeWithJobs, oldEmployee: EmployeeWithJobs) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).employeeDao()
                .updateEmployeeWithRefs(newEmployee, oldEmployee)
            refresh()
        }
    }

    fun initGloriasServers() {


        arrayListOf(

            EmployeeWithJobs(
                Employee(
                    0,
                    0,
                    "Ricardo Arriaga"
                ), arrayListOf(Job("server"),Job("manager"))
            ),
            EmployeeWithJobs(
                Employee(
                    1,
                    1,
                    "Pedro Reyes"
                ), arrayListOf(Job("server"),Job("manager"))
            ),
            EmployeeWithJobs(
                Employee(
                    2,
                    2,
                    "Alan Ramirez"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    3,
                    3,
                    "Ana Ramirez"
                ), arrayListOf(Job("server"),Job("bartender"))
            ),
            EmployeeWithJobs(
                Employee(
                    4,
                    4,
                    "Briana Avelar"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    5,
                    5,
                    "Carla Gutierrez"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    6,
                    6,
                    "Carlos Paredes"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    7,
                    7,
                    "Christina Rojas"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    8,
                    8,
                    "Eireen Caraveo"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    9,
                    9,
                    "Erick Campos"
                ), arrayListOf(Job("bartender"),Job("foodrunner"))
            ),
            EmployeeWithJobs(
                Employee(
                    10,
                    10,
                    "Fernanda Rios"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    11,
                    11,
                    "Hanson Hernandez"
                ), arrayListOf(Job("server"),Job("bartender"),Job("foodrunner"))
            ),
            EmployeeWithJobs(
                Employee(
                    12,
                    12,
                    "Jose Hernandez"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    13,
                    13,
                    "Kayla De La Fuente"
                ), arrayListOf(Job("hostess"))
            ),
            EmployeeWithJobs(
                Employee(
                    14,
                    14,
                    "Krista Johnson"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    15,
                    15,
                    "Lizbhet Sanchez"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    16,
                    16,
                    "Lorena Montano"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    17,
                    17,
                    "Maria Parra"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    18,
                    18,
                    "Maribeth Sims"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    19,
                    19,
                    "Mariela Hernandez"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    20,
                    20,
                    "Melissa Garcia"
                ), arrayListOf(Job("server"),Job("hostess"))
            ),
            EmployeeWithJobs(
                Employee(
                    21,
                    21,
                    "Melissa Gutierrez"
                ), arrayListOf(Job("hostess"))
            ),
            EmployeeWithJobs(
                Employee(
                    22,
                    22,
                    "Oscar Garcia"
                ), arrayListOf(Job("foodrunner"))
            ),
            EmployeeWithJobs(
                Employee(
                    23,
                    23,
                    "Paul Gerardo"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    24,
                    24,
                    "Scarlett Rivas-Conde"
                ), arrayListOf(Job("hostess"))
            ),
            EmployeeWithJobs(
                Employee(
                    25,
                    25,
                    "Tania Palomino"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    26,
                    26,
                    "Ulyssa Fernandez"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    27,
                    27,
                    "Yesica Galindo"
                ), arrayListOf(Job("server"))
            ),
            EmployeeWithJobs(
                Employee(
                    28,
                    28,
                    "Yessica Mandujano"
                ), arrayListOf(Job("server"))
            )
        ).map {
            createNewEmployee(it)
        }

    }
}