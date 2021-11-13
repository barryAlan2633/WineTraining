package com.barryalan.winetraining.ui.manager.floor

import com.barryalan.winetraining.model.floor.TableType

interface TableTypeCallback {
    fun onTableTypePressed(tableType:TableType)
}