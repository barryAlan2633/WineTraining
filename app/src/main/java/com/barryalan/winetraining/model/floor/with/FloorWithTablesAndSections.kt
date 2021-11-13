package com.barryalan.winetraining.model.floor.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.floor.Floor
import com.barryalan.winetraining.model.floor.Section
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.refrerence.FloorSectionRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableRef

data class FloorWithTablesAndSections(
    @Embedded
    val floor: Floor,

    @Relation(
        parentColumn = "floorID",
        entity = Table::class,
        entityColumn = "tableID",
        associateBy = Junction(
            value = FloorTableRef::class,
            parentColumn = "floorID",
            entityColumn = "tableID"
        )
    )
    var allTables: MutableList<Table>,

    @Relation(
        parentColumn = "floorID",
        entity = Section::class,
        entityColumn = "sectionID",
        associateBy = Junction(
            value = FloorSectionRef::class,
            parentColumn = "floorID",
            entityColumn = "sectionID"
        )
    )
    var allSections: MutableList<SectionWithTables>
)
