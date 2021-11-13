package com.barryalan.winetraining.ui.menu.ingredients


import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class IngredientDetailViewModel(application: Application) : BaseViewModel(application) {

    val selectedIngredientLiveData = MutableLiveData<Ingredient>()

    fun fetch(ingredientID: Long) {
        retrieveIngredientFromDB(ingredientID)
    }

    private fun retrieveIngredientFromDB(ingredientID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val ingredient =
                AppDatabase(getApplication()).foodDao().getIngredient(ingredientID)

            withContext(Dispatchers.Main) {
                selectedIngredientLiveData.value = ingredient
            }
        }
    }

    fun updateIngredient(updatedIngredient: Ingredient): Job {
        return viewModelScope.launch(Dispatchers.IO) {

            //make sure the selected ingredient has been fetched
            selectedIngredientLiveData.value?.let { selectedIngredient ->

                //Make sure the updated ingredient has the same ID as the old one allowing update
                updatedIngredient.id = selectedIngredient.id

                //update the ingredient
                updatedIngredient.name = updatedIngredient.name.toLowerCase(Locale.ROOT)
                AppDatabase(getApplication()).foodDao().updateIngredient(updatedIngredient)
            }

            if (selectedIngredientLiveData.value == null) {
                Log.d("Error: ", "Selected ingredient has not been retrieved")
            }
        }
    }

}
