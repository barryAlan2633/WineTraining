package com.barryalan.winetraining.ui.manager.employee

import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs

interface EmployeeCallback {
    fun employeeClicked(employee: EmployeeWithJobs)
}