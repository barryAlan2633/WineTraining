package com.barryalan.winetraining.ui.training.play

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Question
import com.barryalan.winetraining.model.menu.Score
import com.barryalan.winetraining.model.menu.Wine
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import com.barryalan.winetraining.ui.shared.Util
import com.barryalan.winetraining.ui.shared.Util.NOT_PLAYING
import com.barryalan.winetraining.ui.shared.Util.WHITE
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class PlayViewModel(application: Application) : BaseViewModel(application) {

    val winesLiveData = MutableLiveData<List<Wine>>()
    val questionsLiveData = MutableLiveData<List<Question>>()
    val time = MutableLiveData<Long>()

    var selectedQuestionForThisTurn: Question? = null
    var answerWineForThisTurn: Wine? = null
    private var countDownTimer: CountDownTimer? = null

    val selectedWinesForThisTurn = mutableListOf<Wine>()

    var answer1Bkg = WHITE
    var answer2Bkg = WHITE
    var answer3Bkg = WHITE
    var answerBtnClickable = true


    var answerIndex = -1
    var livesLeft = 3
    var leavingDialogShow = false
    var scoreDialogShow = false
    var currentScore = 0
    var userState = NOT_PLAYING
    var chosenType = -1

    fun getWines() {
        viewModelScope.launch(Dispatchers.IO) {
//            val wines = AppDatabase(getApplication()).drinkDao().getAllWines()

//            withContext(Dispatchers.Main) {
//                winesLiveData.value = wines
//            }
        }
    }

    fun getQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            val questions = AppDatabase(getApplication()).questionDao().getAllQuestions()

            withContext(Dispatchers.Main) {
                questionsLiveData.value = questions
            }
        }
    }


    fun generateQuestion() {
        selectedQuestionForThisTurn = null
        val randomGenerator = Random()

        while (selectedQuestionForThisTurn == null) {
            val possibleQuestion: Question =
                questionsLiveData.value!![randomGenerator.nextInt(questionsLiveData.value!!.size)]

            if (chosenType == Util.CATEGORIES && possibleQuestion.type == "category") {
                selectedQuestionForThisTurn = possibleQuestion
            } else if (chosenType == Util.PRICES && possibleQuestion.type == "glass" || possibleQuestion.type == "bottle") {
                selectedQuestionForThisTurn = possibleQuestion
            } else if (chosenType == Util.ALL) {
                selectedQuestionForThisTurn = possibleQuestion
            }

        }
    }

    fun generateAnswerAndChoices() {
        //Add three random wines with different categories to an empty CurrentQuestionWines List
        selectedWinesForThisTurn.clear()
        val r = Random()

        //Add first wine
        selectedWinesForThisTurn.add(winesLiveData.value!![r.nextInt(winesLiveData.value!!.size)])

        //Add second and third wines
        var winesAdded = 1
        while (winesAdded < 3) {
            val randomWineIndex = r.nextInt(winesLiveData.value!!.size)
            val possibleWine: Wine = winesLiveData.value!![randomWineIndex]

            if (!doesSelectedWineListContain(selectedQuestionForThisTurn!!.type, possibleWine)) {
                selectedWinesForThisTurn.add(winesLiveData.value!![randomWineIndex])
                winesAdded++
            }
        }

        //Shuffle the list to make it more randomized
        selectedWinesForThisTurn.shuffle()

        //Choosing an answer at random out of the three wines that were selected
        if (selectedWinesForThisTurn.isNotEmpty()) {
            answerWineForThisTurn =
                selectedWinesForThisTurn[Random().nextInt(selectedWinesForThisTurn.size)]
            answerIndex = selectedWinesForThisTurn.indexOf(answerWineForThisTurn)
        }
    }

    private fun doesSelectedWineListContain(type: String, possibleWine: Wine): Boolean {
        if (selectedWinesForThisTurn.contains(possibleWine)) {
            return true
        }
        for (selectedWine in selectedWinesForThisTurn) {
            //Checking if whatever the answer is is already in the array
            when (type) {
                "category" -> {
                    if (possibleWine.category == selectedWine.category) {
                        return true
                    }
                }
                "glass" -> {
                    if (possibleWine.glassPrice == selectedWine.glassPrice) {
                        return true
                    }
                }
                "bottle" -> {
                    if (possibleWine.bottlePrice == selectedWine.bottlePrice) {
                        return true
                    }
                }
                else -> {
                    throw java.lang.IllegalStateException("Unexpected value: " + selectedQuestionForThisTurn?.type)
                }
            }
        }
        return false
    }

    fun cancelCountDownTimer() {
        countDownTimer!!.cancel()
    }

    fun saveScore(name: String, score: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            DateTimeFormatter.ISO_INSTANT.format(Instant.now())

            val newScore = Score(
                "", name, score, chosenType, DateTimeFormatter
                    .ofPattern("MMM dd, yyyy;HH:mm:ss.SSSSSS")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())
            )

            // Write a message to the database
            val database =
                FirebaseDatabase.getInstance("https://winetraining-d6390-default-rtdb.firebaseio.com/")
            val firebaseRef = database.getReference("Scores")

            firebaseRef.push().apply {
                newScore.id = key.toString()
                setValue(newScore)
            }

            AppDatabase(getApplication()).scoreDao().insertReplace(newScore)
        }
    }


    fun checkAnswer(chosenAnswer: String): Boolean {

        //Reduce the number of lives by one
        when (selectedQuestionForThisTurn?.type) {

            "category" -> if (answerWineForThisTurn?.category.equals(chosenAnswer)) {
                currentScore += 1
                return true
            } else {
                return false
            }

            "glass" -> if ((answerWineForThisTurn?.glassPrice == 0f && chosenAnswer == "Not Sold") ||
                (chosenAnswer != "Not Sold" && answerWineForThisTurn?.glassPrice == chosenAnswer.toFloat())
            ) {
                currentScore += 1
                return true
            } else {
                return false
            }

            "bottle" -> if ((answerWineForThisTurn?.bottlePrice == 0f && chosenAnswer == "Not Sold") ||
                (chosenAnswer != "Not Sold" && answerWineForThisTurn?.bottlePrice == chosenAnswer.toFloat())
            ) {
                currentScore += 1
                return true
            } else {
                return false
            }
            else -> throw IllegalStateException("Unexpected value: " + selectedQuestionForThisTurn?.type)
        }
    }


    fun setCountDownTimer() {
        countDownTimer = object : CountDownTimer(10000, 1) {
            override fun onTick(millisUntilFinished: Long) {
                time.value = millisUntilFinished
            }

            override fun onFinish() {
                time.value = 0.toLong()
            }
        }.start()
    }


}



//todo allow user to upload pictures of the wines
//todo update game to test for wines using the pictures
