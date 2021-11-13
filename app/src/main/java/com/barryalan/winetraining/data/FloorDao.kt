package com.barryalan.winetraining.data

import androidx.room.*
import com.barryalan.winetraining.model.employee.reference.ServerSectionRef
import com.barryalan.winetraining.model.floor.*
import com.barryalan.winetraining.model.floor.refrerence.FloorSectionRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableTypeRef
import com.barryalan.winetraining.model.floor.refrerence.SectionTableRef
import com.barryalan.winetraining.model.floor.with.FloorWithTableTypes
import com.barryalan.winetraining.model.floor.with.FloorWithTablesAndSections
import com.barryalan.winetraining.model.floor.with.FloorWithTablesTableTypes
import java.util.*

@Dao
interface FloorDao {

    //Floor dao ====================================================================================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloor(floor: Floor): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloorTableTypeRef(floorTableTypeRef: FloorTableTypeRef): Long

    @Update
    suspend fun updateFloor(floor: Floor): Int

    @Query("SELECT * FROM FLOOR")
    suspend fun getAllFloors(): List<Floor>

    @Query("DELETE FROM Floor where floorID = :floorID")
    suspend fun deleteFloor(floorID: Long)

    @Transaction
    suspend fun insertFloorWithTableType(floorWithTableTypes: FloorWithTableTypes) {

        floorWithTableTypes.floor.name = floorWithTableTypes.floor.name.toLowerCase(Locale.ROOT)

        var floorId = insertFloor(floorWithTableTypes.floor)

        var count = 2
        val name = floorWithTableTypes.floor.name
        while (floorId == -1L) {
            floorWithTableTypes.floor.name = (name.plus(count)).toLowerCase(Locale.ROOT)

            floorId = insertFloor(floorWithTableTypes.floor)
            count++
        }


        floorWithTableTypes.allTableTypes.map {
            val tableTypeID = insertTableType(it)

            insertFloorTableTypeRef(
                FloorTableTypeRef(floorId, tableTypeID)
            )
        }

    }


    @Transaction
    suspend fun updateFloorWithTableType(
        newFloor: FloorWithTableTypes,
        oldFloor: FloorWithTableTypes
    ) {
        newFloor.floor.id = oldFloor.floor.id
        newFloor.floor.name = newFloor.floor.name.toLowerCase(Locale.ROOT)
        updateFloor(newFloor.floor)

        val tableTypesToAdd = newFloor.allTableTypes.filter { !oldFloor.allTableTypes.contains(it) }
        val tablesTypesToDelete =
            oldFloor.allTableTypes.filter { !newFloor.allTableTypes.contains(it) }

        tablesTypesToDelete.map {
            deleteTableType(it.id)
            deleteFloorTableTypeRef(it.id)
        }

        tableTypesToAdd.map {
            val tableTypeID = insertTableType(it)
            insertFloorTableTypeRef(FloorTableTypeRef(oldFloor.floor.id, tableTypeID))
        }


    }

    @Query("SELECT * FROM Floor WHERE floorID = :floorID")
    suspend fun getFloorWithTableType(floorID: Long): FloorWithTableTypes


    @Transaction
    suspend fun deleteFloorAndAllRefs(floorID: Long) {
        deleteFloor(floorID)

        //delete all other refs
    }


    //Table Type dao ===============================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTableType(tableType: TableType): Long


    @Query("DELETE FROM TableType where tableTypeID = :tableTypeID")
    suspend fun deleteTableType(tableTypeID: Long)

    @Query("DELETE FROM FloorTableTypeRef where tableTypeID = :tableTypeID")
    suspend fun deleteFloorTableTypeRef(tableTypeID: Long)

    @Query("SELECT * FROM Floor WHERE floorID = :floorID")

    suspend fun getFloorWithTablesTableTypes(floorID: Long): FloorWithTablesTableTypes

    //Table dao =====================================================================================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTable(table: Table): Long

    @Update
    suspend fun updateTable(selectedTable: Table): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloorTableRef(floorTableRef: FloorTableRef): Long

    @Transaction
    suspend fun insertTableWithRef(floorID: Long, table: Table) {
        val tableID = insertTable(table)

        insertFloorTableRef(
            FloorTableRef(
                floorID,
                tableID
            )
        )
    }

    @Query("DELETE FROM `TABLE` where tableID = :tableID")
    suspend fun deleteTable(tableID: Long)


    @Query("DELETE FROM FloorTableRef where tableID = :tableID")
    suspend fun deleteFloorTableRef(tableID: Long)


    @Transaction
    suspend fun deleteTableWithRef(tableID: Long) {
        deleteTable(tableID)
        deleteFloorTableRef(tableID)
    }

    //Sections Dao==================================================================================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSection(section: Section): Long

    @Query("DELETE FROM Section where sectionID = :sectionID")
    suspend fun deleteSection(sectionID: Long)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSectionTableRef(sectionTableRef: SectionTableRef): Long

    @Transaction
    suspend fun insertSectionTableRefAndDeleteOthers(
        sectionTableRef: SectionTableRef,
        numberOfServers: Int
    ) {

        val itemAlreadyExisted = insertSectionTableRef(sectionTableRef)

        if (itemAlreadyExisted == -1L) {
            deleteSectionTableRef(sectionTableRef)
        }

        deleteSectionTableRef(sectionTableRef.tableID, numberOfServers, sectionTableRef.sectionID)

    }

    @Query("DELETE FROM SectionTableRef WHERE tableID = :tableID AND numberOfServers = :numberOfServers AND sectionID !=:sectionID ")
    suspend fun deleteSectionTableRef(tableID: Long, numberOfServers: Int, sectionID: Long)


    @Delete
    suspend fun deleteSectionTableRef(sectionTableRef: SectionTableRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloorSectionRef(floorSectionRef: FloorSectionRef): Long

    @Delete
    suspend fun deleteFloorSectionRef(floorSectionRef: FloorSectionRef)


    @Query("SELECT * FROM Floor WHERE floorID = :floorID")
    suspend fun getFloorWithTablesAndSections(floorID: Long): FloorWithTablesAndSections

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSectionColor(sectionColor: SectionColor)


    @Query("SELECT * FROM SectionColor")
    suspend fun getAllSectionColors(): List<SectionColor>

    @Transaction
    suspend fun insertSectionWithRef(section: Section, floorID: Long) {
        val sectionId = insertSection(section)

        if (sectionId != -1L) {

            insertFloorSectionRef(FloorSectionRef(floorID, sectionId))
        }

    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertServerSectionRef(serverSectionRef: ServerSectionRef)

    @Transaction
    fun insertServerSectionRefDeleteOther(serverSectionRef: ServerSectionRef) {
        deleteAllFloorServerSectionRefs(serverSectionRef.floorID)
        insertServerSectionRef(serverSectionRef)
    }


    @Query("DELETE FROM ServerSectionRef WHERE floorID = :floorID")
    fun deleteAllFloorServerSectionRefs(floorID: Long)

    @Query("DELETE FROM ServerSectionRef WHERE floorID = :floorId AND employeeID = :employeeID")
    fun deleteAllServerSectionRefs(floorId: Long, employeeID: Long)

}