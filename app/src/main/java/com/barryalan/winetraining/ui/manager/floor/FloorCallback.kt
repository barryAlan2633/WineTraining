package com.barryalan.winetraining.ui.manager.floor

interface FloorCallback {
    fun onEditFloor(selectedFloorID:Long)
    fun onArrangeTables(selectedFloorID:Long)
    fun onEditSections(selectedFloorID:Long)
}