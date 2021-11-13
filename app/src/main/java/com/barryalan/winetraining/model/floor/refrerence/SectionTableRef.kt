package com.barryalan.winetraining.model.floor.refrerence

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "SectionTableRef", primaryKeys = ["sectionID", "tableID", "numberOfServers"])
data class SectionTableRef(
    @ColumnInfo(name = "sectionID")
    var sectionID: Long,
    @ColumnInfo(name = "tableID")
    var tableID: Long,
    @ColumnInfo(name = "numberOfServers")
    var numberOfServers: Int
)