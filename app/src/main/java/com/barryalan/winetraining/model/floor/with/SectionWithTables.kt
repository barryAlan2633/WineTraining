package com.barryalan.winetraining.model.floor.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.floor.Section
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.refrerence.SectionTableRef

data class SectionWithTables(
    @Embedded
    val section: Section,

    @Relation(
        parentColumn = "sectionID",
        entity = Table::class,
        entityColumn = "tableID",
        associateBy = Junction(
            value = SectionTableRef::class,
            parentColumn = "sectionID",
            entityColumn = "tableID"
        )
    )
    var tables: MutableList<Table>,
)