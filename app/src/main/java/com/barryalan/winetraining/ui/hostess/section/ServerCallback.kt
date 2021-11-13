package com.barryalan.winetraining.ui.hostess.section

import com.barryalan.winetraining.model.employee.with.ServerWithSection

interface ServerCallback {
    fun onServerChecked(server: ServerWithSection)
}