package com.barryalan.winetraining.data

import androidx.room.*
import com.barryalan.winetraining.model.menu.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(vararg question: Question)

    @Delete
    fun delete(question: Question)

    @Query("DELETE FROM table_question")
    fun deleteAllQuestions()

    @Query("SELECT * FROM table_question ORDER BY type,question")
    fun getAllQuestions(): MutableList<Question>

}