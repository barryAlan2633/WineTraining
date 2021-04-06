package com.barryalan.winetraining.shared

import androidx.room.*

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg wine: Wine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wine: Wine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg question: Question)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(score: Score)

    @Delete
    fun delete(wine: Wine)

    @Delete
    fun delete(question: Question)

    @Delete
    fun delete(score: Score)

    @Query("DELETE FROM table_wine")
    fun deleteAllWines()

    @Query("DELETE FROM table_question")
    fun deleteAllQuestions()

    @Query("DELETE FROM table_score")
    fun deleteAllScores()

    @Query("SELECT * FROM table_wine ORDER BY category, name")
    fun getAllWines(): MutableList<Wine>

    @Query("SELECT * FROM table_question ORDER BY type,question")
    fun getAllQuestions(): MutableList<Question>

    @Query("SELECT * FROM table_score ORDER BY type, score")
    fun getAllScores(): MutableList<Score>
}