package com.barryalan.winetraining.model.floor.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.floor.Floor
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.TableType
import com.barryalan.winetraining.model.floor.refrerence.FloorTableRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableTypeRef

data class FloorWithTablesTableTypes(
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
        entity = TableType::class,
        entityColumn = "tableTypeID",
        associateBy = Junction(
            value = FloorTableTypeRef::class,
            parentColumn = "floorID",
            entityColumn = "tableTypeID"
        )
    )
    var allTableTypes: MutableList<TableType>
)
