package com.barryalan.winetraining.model.employee.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.Job
import com.barryalan.winetraining.model.employee.reference.EmployeeJobRef

data class EmployeeWithJobs(

    @Embedded
    val employee: Employee,

    @Relation(
        parentColumn = "employeeID",
        entity = Job::class,
        entityColumn = "name",
        associateBy = Junction(
            value = EmployeeJobRef::class,
            parentColumn = "employeeID",
            entityColumn = "jobName"
        )
    )
    val jobs: List<Job>
)
