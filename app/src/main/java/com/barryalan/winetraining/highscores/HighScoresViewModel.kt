package com.barryalan.winetraining.highscores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.shared.Score
import com.barryalan.winetraining.shared.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HighScoresViewModel(application: Application):AndroidViewModel(application) {
    val scoresLiveData = MutableLiveData<MutableList<Score>>()
    var gameTypeLiveData = MutableLiveData(-1)

    fun getScores() {
        viewModelScope.launch(Dispatchers.IO) {
            val scores = AppDatabase(getApplication()).appDao().getAllScores()

            withContext(Dispatchers.Main) {
                scoresLiveData.value = scores
            }
        }
    }
}