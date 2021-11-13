package com.barryalan.winetraining.ui.shared

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.barryalan.winetraining.data.*
import com.barryalan.winetraining.model.customer.Customer
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.Job
import com.barryalan.winetraining.model.employee.reference.EmployeeJobRef
import com.barryalan.winetraining.model.employee.reference.ServerSectionRef
import com.barryalan.winetraining.model.floor.*
import com.barryalan.winetraining.model.floor.refrerence.FloorSectionRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableTypeRef
import com.barryalan.winetraining.model.floor.refrerence.SectionTableRef
import com.barryalan.winetraining.model.menu.*
import com.barryalan.winetraining.model.menu.reference.*


@Database(
    version = 37,
    entities = [
        Wine::class,
        Question::class,
        Score::class,

        //Menu
        Ingredient::class,
        Recipe::class,
        MenuItem::class,
        Menu::class,

        Amount::class,
        Price::class,
        Instruction::class,

        RecipeIngredientRef::class,
        RecipeInstructionRef::class,

        MenuItemRecipeRef::class,
        MenuMenuItemRef::class,
        MenuItemInstructionRef::class,

        //Employee
        Job::class,
        Employee::class,
        EmployeeJobRef::class,

        //Floor
        Floor::class,

        TableType::class,
        FloorTableTypeRef::class,

        Table::class,
        FloorTableRef::class,

        Section::class,
        SectionColor::class,
        SectionTableRef::class,
        FloorSectionRef::class,

        ServerSectionRef::class,

        Customer::class


    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao
    abstract fun questionDao(): QuestionDao
    abstract fun scoreDao(): ScoreDao
    abstract fun foodDao(): FoodDao
    abstract fun menuDao(): MenuDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun floorDao(): FloorDao
    abstract fun customerDao(): CustomerDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: buildDatabase(context).also {
                INSTANCE = it
            }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java, "app-database"
            ).fallbackToDestructiveMigration()
                .build()

    }
}