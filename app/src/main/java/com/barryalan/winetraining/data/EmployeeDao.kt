package com.barryalan.winetraining.data

import androidx.room.*
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.Job
import com.barryalan.winetraining.model.employee.reference.EmployeeJobRef
import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs
import com.barryalan.winetraining.model.employee.with.ServerWithSection

@Dao
interface EmployeeDao {

    //Employee======================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employee: Employee): Long

    @Insert
    suspend fun insertAllEmployees(employees: List<Employee>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateEmployee(employee: Employee): Int


    @Query("SELECT * FROM Employee WHERE employeeID = :ID")
    suspend fun getEmployeeByID(ID: Long): Employee

    @Query("SELECT * FROM Employee")
    fun getAllEmployeeWithJobs(): List<EmployeeWithJobs>

    @Query("DELETE FROM Employee Where employeeID = :employeeID")
    suspend fun deleteEmployee(employeeID: Long)


    @Transaction
    suspend fun insertEmployeeWithRefs(employee: EmployeeWithJobs) {
        val employeeID = insertEmployee(employee.employee)

        employee.jobs.map {
            insertEmployeeJobRef(EmployeeJobRef(employeeID, it.name))
        }
    }

    @Transaction
    suspend fun updateEmployeeWithRefs(
        newEmployee: EmployeeWithJobs,
        oldEmployee: EmployeeWithJobs
    ) {

        updateEmployee(newEmployee.employee)

        //jobs to add
        newEmployee.jobs.filter { !oldEmployee.jobs.contains(it) }.map {
            insertEmployeeJobRef(
                EmployeeJobRef(newEmployee.employee.id, it.name)
            )
        }

        //jobs to remove
        oldEmployee.jobs.filter { !newEmployee.jobs.contains(it) }.map {
            deleteEmployeeJobRef(
                newEmployee.employee.id, it.name
            )
        }
    }

    @Transaction
    suspend fun deleteEmployeeWithRefs(employeeID: Long) {
        deleteEmployee(employeeID)
        deleteEmployeeWithRefs(employeeID)
    }

    @Transaction
    suspend fun getTablesServerIDFromTableID(tableID:Long){

    }

    //Jobs==========================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJob(job: Job): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllJobs(jobs: List<Job>): List<Long>

    //Refs==========================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployeeJobRef(employeeJobRef: EmployeeJobRef): Long

    @Query("DELETE FROM employeeJobRef Where employeeID = :employeeID AND jobName = :jobName")
    suspend fun deleteEmployeeJobRef(employeeID: Long, jobName: String)

    @Transaction
    suspend fun getAllServers(): List<Employee> {
        val serverIDs = getAllServerIDsFromEmployeeRefs()

        val servers = arrayListOf<Employee>()
        serverIDs.map { id ->
            servers.add(getEmployeeByID(id))
        }
        return servers
    }

    @Query("SELECT employeeID FROM EmployeeJobRef WHERE jobName = 'server'")
    suspend fun getAllServerIDsFromEmployeeRefs(): List<Long>


    @Query("SELECT * FROM Employee")
    fun getAllServersWithSections(): List<ServerWithSection>

}