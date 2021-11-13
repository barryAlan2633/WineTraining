package com.barryalan.winetraining.ui.training.questionList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Question
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuestionListViewModel(application: Application) : BaseViewModel(application) {

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
            AppDatabase(getApplication()).questionDao().insertReplace(question)
        }
        getQuestions()
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).questionDao().delete(question)
        }
        getQuestions()
    }

    fun getQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            val questions = AppDatabase(getApplication()).questionDao().getAllQuestions()

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

            AppDatabase(getApplication()).questionDao().insertReplace(*allQuestions.toTypedArray())
        }
    }

}
