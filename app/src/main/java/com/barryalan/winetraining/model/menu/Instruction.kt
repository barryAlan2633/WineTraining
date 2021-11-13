package com.barryalan.winetraining.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Instruction(

    @ColumnInfo(name="instructionNumber")
    var instructionNumber:Int?,

    @ColumnInfo(name="instructionText")
    val instructionText:String
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="instructionID")
    var id:Long = 0
}
