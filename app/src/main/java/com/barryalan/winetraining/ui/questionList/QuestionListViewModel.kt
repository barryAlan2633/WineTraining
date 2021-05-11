package com.barryalan.winetraining.ui.questionList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.Question
import com.barryalan.winetraining.ui.shared.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionListViewModel(application: Application) : AndroidViewModel(application) {

    val questionsLiveData = MutableLiveData<MutableList<Question>>()
    private var prePopulateApp = 0

    fun prePopulateQuestions(){
        prePopulateApp++
        if(prePopulateApp == 20){
            initQuestions()
        }
    }

    fun saveQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).appDao().insertReplace(question)
        }
        getQuestions()
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).appDao().delete(question)
        }
        getQuestions()
    }

    fun getQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            val questions = AppDatabase(getApplication()).appDao().getAllQuestions()

            withContext(Dispatchers.Main) {
                questionsLiveData.value = questions
            }
        }
    }

    private fun initQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            val allQuestions = mutableListOf<Question>()

            //Category
            allQuestions.add(Question("category", "What category of wine is #name!?"))
            allQuestions.add(Question("category", "Which of these categories does #name! belong to?"))

            //Bottle
            allQuestions.add(Question("bottle", "What is the bottle price of #name!?"))
            allQuestions.add(Question("bottle", "How much is a bottle of #name!?"))

            //Glass
            allQuestions.add(Question("glass", "What is the glass price of #name!?"))
            allQuestions.add(Question("glass", "How much is the glass of #name!?"))

            AppDatabase(getApplication()).appDao().insertReplace(*allQuestions.toTypedArray())
        }
    }

}
