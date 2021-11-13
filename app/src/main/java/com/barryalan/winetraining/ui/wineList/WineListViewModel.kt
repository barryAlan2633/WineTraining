package com.barryalan.winetraining.ui.wineList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Wine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WineListViewModel(application: Application) : AndroidViewModel(application) {

    val winesLiveData = MutableLiveData<MutableList<Wine>>()
    private var prePopulateApp = 0

    fun prePopulateWine(){
        prePopulateApp++
        if(prePopulateApp == 20){
            initWines()
        }
    }

    fun saveWine(wine: Wine) {
//        viewModelScope.launch(Dispatchers.IO) {
//            AppDatabase(getApplication()).drinkDao().insertReplace(wine)
//        }
        getWines()
    }

    fun deleteWine(wine: Wine) {
//        viewModelScope.launch(Dispatchers.IO) {
//            AppDatabase(getApplication()).drinkDao().delete(wine)
//        }
        getWines()
    }

    fun getWines() {
        viewModelScope.launch(Dispatchers.IO) {
//            val wines = AppDatabase(getApplication()).drinkDao().getAllWines()
//
//            withContext(Dispatchers.Main) {
//                winesLiveData.value = wines
//            }
        }
    }

    private fun initWines() {
        viewModelScope.launch(Dispatchers.IO) {
            val allWines = mutableListOf<Wine>()
            //House
            allWines.add(Wine("Frontera Chardonnay", "House", 8F, 0F))
            allWines.add(Wine("Frontera Carmenere", "House", 8F, 0F))
            allWines.add(Wine("Sangria", "House", 7F, 0F))
            allWines.add(Wine("Placido Pinot Grigio", "House", 8F, 0F))

            //Sparkling
            allWines.add(Wine("Maschio Prosecco", "Sparkling", 0F, 8F))
            allWines.add(Wine("Maschio Rose Sparkling", "Sparkling", 0F, 8F))
            allWines.add(Wine("La Marca Prosecco", "Sparkling", 0F, 30F))
            allWines.add(Wine("Ruffino Sparkling Rose", "Sparkling", 0F, 30F))
            allWines.add(Wine("Ruffino Prosecco", "Sparkling", 0F, 33F))
            allWines.add(Wine("Chandon Brut", "Sparkling", 0F, 48F))

            //Pinot Grigio
            allWines.add(Wine("Placido", "Pinot Grigio", 8F, 25F))
            allWines.add(Wine("Estancia", "Pinot Grigio", 8F, 25F))

            //Sauvignon Blanc
            allWines.add(Wine("Nimbus", "Sauvignon Blanc", 9F, 30F))
            allWines.add(Wine("Kim Crawford", "Sauvignon Blanc", 11F, 44F))

            //Chardonnay
            allWines.add(Wine("Meiomi", "Chardonnay", 10F, 38F))
            allWines.add(Wine("Rodney Strong Chalk Hill", "Chardonnay", 0F, 36F))
            allWines.add(Wine("Kendall-Jackson 'Vintner's reserve'", "Chardonnay", 11F, 36F))
            allWines.add(Wine("Ferrari-Carrano", "Chardonnay", 13F, 44F))
            allWines.add(Wine("Rombauer", "Chardonnay", 0F, 75F))

            //Other Whites
            allWines.add(Wine("Matua Valley Pinot Noir Rose", "Other Whites", 8F, 33F))
            allWines.add(Wine("Bieler Rose", "Other Whites", 9F, 35F))
            allWines.add(Wine("Meiomi Rose", "Other Whites", 0F, 40F))
            allWines.add(Wine("Miraval Provence Rose", "Other Whites", 0F, 46F))
            allWines.add(Wine("Conundrum By Caymus", "Other Whites", 12F, 44F))
            allWines.add(Wine("Blindfold", "Other Whites", 0F, 48F))

            //Malbec
            allWines.add(Wine("Dineño Mendoza", "Malbec", 9F, 34F))
            allWines.add(Wine("Trivento 'Amado Sur'", "Malbec", 0F, 40F))
            allWines.add(Wine("Pascual Toso Reserve", "Malbec", 13F, 42F))
            allWines.add(Wine("Gascon Reserve", "Malbec", 0F, 50F))

            //Pinot Noir
            allWines.add(Wine("Mark West", "Pinot Noir", 9F, 33F))
            allWines.add(Wine("Meiomi", "Pinot Noir", 12F, 44F))
            allWines.add(Wine("Diora", "Pinot Noir", 0F, 50F))

            //Red Blend
            allWines.add(Wine("Ravage", "Red Blend", 8F, 32F))
            allWines.add(Wine("19 Crimes", "Red Blend", 8F, 32F))
            allWines.add(Wine("Noble Vines The One", "Red Blend", 0F, 32F))
            allWines.add(Wine("Bogle Phantom", "Red Blend", 0F, 45F))
            allWines.add(Wine("The Prisoner", "Red Blend", 0F, 75F))

            //Merlot
            allWines.add(Wine("Robert Mondavi 'Private Selection'", "Merlot", 8F, 30F))

            //Cabernet Sauvignon
            allWines.add(Wine("Robert Mondavi 'Private Selection'", "Cabernet Sauvignon", 8F, 30F))
            allWines.add(Wine("Ravage", "Cabernet Sauvignon", 0F, 30F))
            allWines.add(Wine("Tom Gore", "Cabernet Sauvignon", 10F, 35F))
            allWines.add(Wine("Louis Martini", "Cabernet Sauvignon", 0F, 38F))
            allWines.add(Wine("Kendall-Jackson 'Vintner's Reserve", "Cabernet Sauvignon", 12F, 40F))
            allWines.add(Wine("Franciscan", "Cabernet Sauvignon", 0F, 58F))
            allWines.add(Wine("Ferrari-Carrano", "Cabernet Sauvignon", 0F, 78F))
            allWines.add(Wine("Jordan", "Cabernet Sauvignon", 0F, 115F))
            allWines.add(Wine("Silver Oak", "Cabernet Sauvignon", 0F, 143F))
            allWines.add(Wine("Caymus", "Cabernet Sauvignon", 0F, 165F))

//            AppDatabase(getApplication()).drinkDao().insertReplace(*allWines.toTypedArray())
        }
    }

}
