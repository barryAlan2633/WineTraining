package com.barryalan.winetraining.ui.shared

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClockInViewModel(application: Application):BaseViewModel(application) {

    val employeeLiveData = MutableLiveData<EmployeeWithJobs>()

    fun retrieveUser(clockInId:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val employee =  AppDatabase(getApplication()).appDao().getEmployeeWithJobs(clockInId)

            withContext(Dispatchers.Main){
                employeeLiveData.value = employee
            }
        }
    }
}