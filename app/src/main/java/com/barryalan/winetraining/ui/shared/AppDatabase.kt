package com.barryalan.winetraining.ui.shared

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.barryalan.winetraining.model.Question
import com.barryalan.winetraining.model.Score
import com.barryalan.winetraining.model.Wine


@Database(
    version = 12, entities = [
        Wine::class,
        Question::class,
        Score::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

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