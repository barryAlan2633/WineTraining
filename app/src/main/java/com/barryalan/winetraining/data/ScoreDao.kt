package com.barryalan.winetraining.data

import androidx.room.*
import com.barryalan.winetraining.model.menu.Score

@Dao
interface ScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(score: Score): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(score: Score): Long

    @Delete
    fun delete(score: Score)

    @Query("DELETE FROM table_score")
    fun deleteAllScores()

    @Query("SELECT * FROM table_score ORDER BY type, score DESC")
    fun getAllScores(): MutableList<Score>
}