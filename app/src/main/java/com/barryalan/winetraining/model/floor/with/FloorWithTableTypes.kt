package com.barryalan.winetraining.model.floor.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.floor.Floor
import com.barryalan.winetraining.model.floor.TableType
import com.barryalan.winetraining.model.floor.refrerence.FloorTableTypeRef

data class FloorWithTableTypes(
    @Embedded
    val floor: Floor,

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