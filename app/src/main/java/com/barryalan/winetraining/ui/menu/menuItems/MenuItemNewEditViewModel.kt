package com.barryalan.winetraining.ui.menu.menuItems

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Instruction
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.with.MenuItemWithRecipes
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.*

class MenuItemNewEditViewModel(application: Application) : BaseViewModel(application) {

    val menuItemToUpdateLiveData = MutableLiveData<MenuItemWithRecipes>()
    val recipesListLiveData = MutableLiveData<List<Recipe>>()

    fun saveMenuItemWithRecipes(
        menuItem: MenuItem,
        recipeList: ArrayList<Recipe>,
        amountsList: List<Amount>,
        instructionsList: List<Instruction>
    ): Job {
        return viewModelScope.launch {
            AppDatabase(getApplication()).menuDao().insertMenuItemWithRecipes(
                menuItem,
                recipeList,
                amountsList,
                instructionsList
            )
        }
    }

    fun fetchSelectedMenuItemWithRecipes(menuItemID: Long) {
        retrieveMenuItemWithRecipesFromDB(menuItemID)
    }

    fun fetchRecipeList() {
        retrieveRecipeListFromDB()
    }

    fun fetchRecipeWithName(recipeName: String): Recipe? {
        recipesListLiveData.value?.map {
            if (it.name == recipeName) {
                return it
            }
        }

        return null
    }

    private fun retrieveMenuItemWithRecipesFromDB(menuItemID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val menuItemToUpdate =
                AppDatabase(getApplication()).menuDao().getMenuItemWithRecipes(menuItemID)

            withContext(Dispatchers.Main) {
                menuItemToUpdateLiveData.value = menuItemToUpdate
            }
        }
    }

    fun updateMenuItemWithRecipes(
        updatedMenuItem: MenuItem,
        updatedRecipeList: ArrayList<Recipe>,
        updatedAmountsList: List<Amount>,
        updatedInstructionList: List<Instruction>
    ): Job {
        return viewModelScope.launch(Dispatchers.IO) {

            //make sure that the recipeToUpdate has been fetched because we need its fields
            menuItemToUpdateLiveData.value?.let { menuItemWithRecipesToUpdate ->

                viewModelScope.launch(Dispatchers.IO) {
                    //update recipeWithIngredients object where it differs from the previous instance
                    AppDatabase(getApplication()).menuDao().updateMenuItemWithRecipes(
                        updatedMenuItem,
                        updatedRecipeList,
                        updatedAmountsList,
                        updatedInstructionList,
                        menuItemWithRecipesToUpdate
                    )
                }

            }
            if (menuItemToUpdateLiveData.value == null) {
                Log.d("Error:", "menuItemToUpdate has not been retrieved")
            }
        }
    }


    private fun retrieveRecipeListFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val recipeList = AppDatabase(getApplication()).foodDao().getAllRecipes()

            withContext(Dispatchers.Main) {
                recipesListLiveData.value = recipeList
            }
        }
    }

    fun initGloriasMenuItems() {
        val gloriasMenuItems = arrayListOf(
            MenuItem("Tamales", null, "food"),
            MenuItem("Pupusas", null, "food"),
            MenuItem("Platano Frito", null, "food"),
            MenuItem("Ceviche Orange Roughy", null, "food"),
            MenuItem("Ceviche Tostadas", null, "food"),
            MenuItem("Ceviche Trio", null, "food"),
            MenuItem("Shrimp Cocktail", null, "food"),
            MenuItem("Chile con Queso Small", null, "food"),
            MenuItem("Chile con Queso Large", null, "food"),
            MenuItem("Chile con Queso", null, "food"),
            MenuItem("Guacamole Small", null, "food"),
            MenuItem("Guacamole Large", null, "food"),
            MenuItem("Guacamole", null, "food"),
            MenuItem("Nachos Beef Fajita", null, "food"),
            MenuItem("Nachos Chicken Fajita", null, "food"),
            MenuItem("Nachos Ground Beef", null, "food"),
            MenuItem("Nachos Shredded Chicken", null, "food"),
            MenuItem("Nachos Shrimp", null, "food"),
            MenuItem("Nachos Brisket", null, "food"),
            MenuItem("Nachos Combination", null, "food"),
            MenuItem("Yuca Salvadorena", null, "food"),
            MenuItem("Queso Fundido", null, "food"),


            MenuItem("Soup Black Bean", null, "food"),
            MenuItem("Soup Chicken", null, "food"),
            MenuItem("Soup Chicken Tortilla", null, "food"),
            MenuItem("Soup Mariscada", null, "food"),
            MenuItem("Salad Salmon Chimichurri", null, "food"),
            MenuItem("Salad Garden", null, "food"),
            MenuItem("Salad Tropical", null, "food"),


            MenuItem("Carne Flameada", null, "food"),
            MenuItem("Carne Asada", null, "food"),
            MenuItem("Plato Salvatex", null, "food"),
            MenuItem("A la Mexicana", null, "food"),
            MenuItem("Mar Y Tierra", null, "food"),
            MenuItem("Grilled Quail", null, "food"),
            MenuItem("Gloria's Super Special", null, "food"),
            MenuItem("Churrasco Tipico", null, "food"),
            MenuItem("Lechon Asado", null, "food"),
            MenuItem("Pollo Asado Combinado", null, "food"),
            MenuItem("Pollo con Champinones", null, "food"),
            MenuItem("Pescado a la Gloria", null, "food"),
            MenuItem("Salmon Costeno", null, "food"),
            MenuItem("Blackened Red Snapper", null, "food"),
            MenuItem("Pescado Acajutla", null, "food"),
            MenuItem("Camarones", null, "food"),
            MenuItem("Quesadillas Chicken Spinach", null, "food"),
            MenuItem("Quesadillas Skirt Steak Fajita", null, "food"),
            MenuItem("Quesadillas Shrimp", null, "food"),
            MenuItem("Quesadillas Brisket", null, "food"),


            MenuItem("Crispy Taco", null, "food"),
            MenuItem("Chicken Flautas", null, "food"),
            MenuItem("Chile Relleno", null, "food"),
            MenuItem("Combination", null, "food"),
            MenuItem("Tostadas", null, "food"),
            MenuItem("Chimichanga", null, "food"),
            MenuItem("Burrito", null, "food"),


            MenuItem("Enchiladas a la Gloria", null, "food"),
            MenuItem("Enchiladas Chicken", null, "food"),
            MenuItem("Enchiladas Beef", null, "food"),
            MenuItem("Enchiladas Combination", null, "food"),
            MenuItem("Enchiladas Chicken Mole", null, "food"),
            MenuItem("Enchiladas Spinach", null, "food"),
            MenuItem("Enchiladas Shrimp", null, "food"),

            MenuItem("Fajitas Single(1)", null, "food"),
            MenuItem("Fajitas Mix(1)", null, "food"),
            MenuItem("Fajitas Shrimp(1)", null, "food"),
            MenuItem("Fajitas Mix Shrimp(1)", null, "food"),
            MenuItem("Fajitas Single(2)", null, "food"),
            MenuItem("Fajitas Mix(2)", null, "food"),
            MenuItem("Fajitas Shrimp(2)", null, "food"),
            MenuItem("Fajitas Mix Shrimp(2)", null, "food"),


            MenuItem("Tacos Red Snapper", null, "food"),
            MenuItem("Tacos Brisket", null, "food"),
            MenuItem("Tacos Shrimp", null, "food"),


            MenuItem("Flan", null, "food"),
            MenuItem("Chocolate Turtle Brownie", null, "food"),
            MenuItem("Churros", null, "food"),
            MenuItem("Honey Banana", null, "food"),
            MenuItem("Tres Leches", null, "food"),
            MenuItem("Chocolate Sundae", null, "food"),


            MenuItem("Nancy's Favorite", null, "food"),
            MenuItem("Carne Encebollada", null, "food"),
            MenuItem("Glenda's Favorite", null, "food")

            //todo add brunch
        )

        gloriasMenuItems.map {
            saveMenuItemWithRecipes(
                it,
                arrayListOf(),
                arrayListOf(),
                arrayListOf()
            )
        }
    }

    fun nukeMenuItems() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).menuDao().nukeMenuItemTable()
        }
    }
}
