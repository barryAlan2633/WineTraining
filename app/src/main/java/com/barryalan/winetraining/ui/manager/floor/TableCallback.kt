package com.barryalan.winetraining.ui.manager.floor

interface TableCallback {
    fun alignTable(position:Int,type:Int)

    fun changeTableRotation(position:Int, rotation:Float)

    fun changeTableNumber(position:Int, newNumber:Int)
}