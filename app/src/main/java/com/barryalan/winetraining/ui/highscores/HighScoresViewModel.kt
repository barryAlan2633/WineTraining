package com.barryalan.winetraining.ui.highscores

import android.app.Application
import androidx.lifecycle.*
import com.barryalan.winetraining.model.Score
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HighScoresViewModel(application: Application) : AndroidViewModel(application) {

//    private val _stateEvent: MutableLiveData<HighScoreStateEvent> = MutableLiveData()
//    private val _viewState: MutableLiveData<HighScoreViewState> = MutableLiveData()
//
//    val viewState: LiveData<HighScoreViewState>
//        get() = _viewState
//
//    val dataState: LiveData<DataState<HighScoreViewState>> =
//        Transformations.switchMap(_stateEvent) { stateEvent ->
//
//            stateEvent?.let {
//                handleStateEvent(it)
//
//            }
//        }
//
//
//    fun handleStateEvent(stateEvent:HighScoreStateEvent): LiveData<DataState<HighScoreViewState>>{
//        when(stateEvent){
//            is HighScoreStateEvent.GetScoresEvent ->{
//                return getScores()
//            }
//
//            is HighScoreStateEvent.None ->{
//                return AbsentLiveData.create()
//            }
//        }
//    }


    val scoresLiveData = MutableLiveData<MutableList<Score>>()
    var gameTypeLiveData = MutableLiveData(-1)


    fun getScores() {
        viewModelScope.launch(Dispatchers.IO) {

            val firebaseScores = mutableListOf<Score>()

            // Write a message to the database
            val database =
                FirebaseDatabase.getInstance("https://winetraining-d6390-default-rtdb.firebaseio.com/")
            val dbRef = database.getReference("Scores")


            val queryValueListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    val snapshotIterator = dataSnapshot.children

                    val iterator: Iterator<DataSnapshot> = snapshotIterator.iterator()
                    while (iterator.hasNext()) {

                        val nextItem = iterator.next()


                        //todo why do i need that elvis down here? .-. 4/27/21
                        val newScore = Score(

                            nextItem.child("id").getValue(String::class.java).toString(),
                            nextItem.child("name").getValue(String::class.java).toString(),
                            nextItem.child("score").getValue(Int::class.java) ?: -1,
                            nextItem.child("type").getValue(Int::class.java) ?: -1,
                            nextItem.child("timeStamp").getValue(String::class.java).toString()

                        )

                        firebaseScores.add(newScore)
                    }

                    viewModelScope.launch(Dispatchers.IO) {
                        firebaseScores.map {
                            AppDatabase(getApplication()).appDao().insertReplace(it)
                        }

                        val scores = AppDatabase(getApplication()).appDao().getAllScores()
                        withContext(Dispatchers.Main) {
                            scoresLiveData.value = scores
                        }


                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {}
            }

            dbRef.addValueEventListener(queryValueListener)


        }
    }

    // 1)get all scores from firebase and save into room when scores tab is opened
    // 2)get all room scores and display into rv when a rb is selected on scores tab
    // 3)when saving a score save into room and firebase
    //todo 4)if saving to firebase failed then save and try again when possible


}

