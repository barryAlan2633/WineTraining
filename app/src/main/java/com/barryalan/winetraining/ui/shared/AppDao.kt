package com.barryalan.winetraining.ui.shared

import androidx.room.*
import com.barryalan.winetraining.model.Question
import com.barryalan.winetraining.model.Score
import com.barryalan.winetraining.model.Wine

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(vararg wine: Wine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(wine: Wine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(vararg question: Question)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(score: Score) : Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(score: Score) : Long

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

    @Query("SELECT * FROM table_score ORDER BY type, score DESC")
    fun getAllScores(): MutableList<Score>
}