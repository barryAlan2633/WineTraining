package com.barryalan.winetraining.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.barryalan.winetraining.model.customer.Customer

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCustomer(newCustomer: Customer): Long

    @Query("SELECT * FROM Customer")
    fun getAllCustomers(): List<Customer>
}