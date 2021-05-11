package com.barryalan.winetraining.data

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.Score
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ScoresRepository(application: Application) {

    suspend fun getScores() {
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

                    val newScore = iterator.next().getValue(Score::class.java)
                    if (newScore != null) {
                        firebaseScores.add(newScore)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        dbRef.addValueEventListener(queryValueListener)



    }
}