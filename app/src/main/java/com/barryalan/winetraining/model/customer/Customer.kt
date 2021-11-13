package com.barryalan.winetraining.model.customer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Customer", indices = [Index(value = ["customerID"], unique = true)])
data class Customer(

    @ColumnInfo(name = "tableID")
    var tableID: Long,

    @ColumnInfo(name = "tableNumber")
    var tableNumber: Int,

    @ColumnInfo(name = "partySize")
    var partySize: Int,

    @ColumnInfo(name = "serverID")
    var serverID: Long

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "customerID")
    var id: Int = 0
}