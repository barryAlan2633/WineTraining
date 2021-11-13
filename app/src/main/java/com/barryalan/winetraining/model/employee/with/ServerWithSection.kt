package com.barryalan.winetraining.model.employee.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.reference.ServerSectionRef
import com.barryalan.winetraining.model.floor.Section
import com.barryalan.winetraining.model.floor.with.SectionWithTables

data class ServerWithSection(

    @Embedded
    val employee: Employee,

    @Relation(
        parentColumn = "employeeID",
        entity = Section::class,
        entityColumn = "sectionID",
        associateBy = Junction(
            value = ServerSectionRef::class,
            parentColumn = "employeeID",
            entityColumn = "sectionID"
        )
    )

    val section: SectionWithTables?

)

