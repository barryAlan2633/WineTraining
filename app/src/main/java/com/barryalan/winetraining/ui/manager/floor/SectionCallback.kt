package com.barryalan.winetraining.ui.manager.floor

import com.barryalan.winetraining.model.floor.Section

interface SectionCallback {
    fun onSectionSelected(sectionId:Long)
}