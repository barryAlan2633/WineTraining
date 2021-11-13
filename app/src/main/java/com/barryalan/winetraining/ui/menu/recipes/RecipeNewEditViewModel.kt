package com.barryalan.winetraining.ui.menu.recipes

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.with.RecipeWithIngredients
import com.barryalan.winetraining.ui.shared.AppDatabase
import com.barryalan.winetraining.ui.shared.BaseViewModel
import kotlinx.coroutines.*
import java.util.*

class RecipeNewEditViewModel(application: Application) : BaseViewModel(application) {

    val recipeToUpdateLiveData = MutableLiveData<RecipeWithIngredients>()
    val ingredientListLiveData = MutableLiveData<List<Ingredient>>()

    fun saveRecipeWithIngredients(newRecipeWithIngredients: RecipeWithIngredients): Job {
        return viewModelScope.launch {
            AppDatabase(getApplication()).foodDao()
                .insertRecipeWithIngredients(newRecipeWithIngredients)
        }
    }

    private fun saveIngredients(ingredients: List<Ingredient>): Job {

        ingredients.map {
            it.name = it.name.toLowerCase(Locale.ROOT)
        }

        Log.e("debug",ingredients.toString())

        return viewModelScope.launch {
            AppDatabase(getApplication()).foodDao().insertAllIngredients(ingredients)
        }
    }

    fun fetchSelectedRecipeWI(recipeID: Long) {
        retrieveRecipeWithIngredientsFromDB(recipeID)
    }

    fun fetchIngredientList() {
        retrieveIngredientListFromDB()
    }

    private fun retrieveRecipeWithIngredientsFromDB(recipeID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipeToUpdate =
                AppDatabase(getApplication()).foodDao().getRecipeWithIngredients(recipeID)

            withContext(Dispatchers.Main) {
                recipeToUpdateLiveData.value = recipeToUpdate
            }
        }
    }

    fun updateRecipeWithIngredients(updatedRecipeWithIngredients: RecipeWithIngredients): Job {
        return viewModelScope.launch(Dispatchers.IO) {

            //make sure that the recipeToUpdate has been fetched because we need its fields
            recipeToUpdateLiveData.value?.let { recipeWithIngredientsToUpdate ->

                viewModelScope.launch(Dispatchers.IO) {
                    //update recipeWithIngredients object where it differs from the previous instance
                    AppDatabase(getApplication()).foodDao().updateRecipeWithIngredients(
                        updatedRecipeWithIngredients,
                        recipeWithIngredientsToUpdate
                    )
                }

            }
            if (recipeToUpdateLiveData.value == null) {
                Log.e("Error:", "recipeToUpdate has not been retrieved")
            }
        }
    }


    private fun retrieveIngredientListFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            val ingredientList = AppDatabase(getApplication()).foodDao().getAllIngredients()

            withContext(Dispatchers.Main) {
                ingredientListLiveData.value = ingredientList
            }
        }
    }

    fun fetchIngredientWithName(ingredientName: String): Ingredient? {
        ingredientListLiveData.value?.map {
            if (it.name == ingredientName) {
                return it
            }
        }

        return null
    }

    fun nukeIngredients() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).foodDao().nukeIngredientTable()
            AppDatabase(getApplication()).foodDao().nukeRecipeIngredientRefTable()

        }
    }

    fun nukeRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase(getApplication()).foodDao().nukeRecipeTable()
            AppDatabase(getApplication()).foodDao().nukeRecipeIngredientRefTable()
        }
    }

    fun initGloriasRecipes() {
        val gloriasRecipes = arrayListOf(
            Recipe("Tamal", null, "food"),
            Recipe("Flour Masa", null, "food"),
            Recipe("Pupusa", null, "food"),
            Recipe("Ceviche Orange Roughy", null, "food"),
            Recipe("Ceviche Scallops", null, "food"),
            Recipe("Nacho", null, "food"),
            Recipe("Plantain Fried", null, "food"),
            Recipe("Yucca Fried", null, "food"),
            Recipe("Queso Fundido", null, "food"),

            Recipe("Tostada", null, "food"),
            Recipe("Tostada Table", null, "food"),
            Recipe("Tostada Small", null, "food"),
            Recipe("Tostada Chilaquiles ", null, "food"),
            Recipe("Tostada Plantain Fried Long", null, "food"),
            Recipe("Tostada Plantain Fried Round", null, "food"),
            Recipe("Taco Shell", null, "food"),

            Recipe("Guacamole", null, "food"),
            Recipe("Pico de Gallo", null, "food"),
            Recipe("Sour Cream Brunch", null, "food"),
            Recipe("Onion Grilled", null, "food"),
            Recipe("Spinach Grilled", null, "food"),

            Recipe("Rice Spanish", null, "food"),
            Recipe("Rice Cazamiento", null, "food"),
            Recipe("Rice Poblano", null, "food"),
            Recipe("Beans Pinto Refried", null, "food"),
            Recipe("Beans Black Refried", null, "food"),
            Recipe("Beans Table Black Refried ", null, "food"),
            Recipe("Vegetables Adult Steamed", null, "food"),
            Recipe("Vegetables Adult Grilled", null, "food"),
            Recipe("Vegetables Kid Grilled", null, "food"),
            Recipe("Vegetables Kid Steamed", null, "food"),
            Recipe("Vegetables Ribeye", null, "food"),
            Recipe("Fries", null, "food"),
            Recipe("Potatoes Rosemary", null, "food"),
            Recipe("Fruit Every Day", null, "food"),
            Recipe("Fruit Brunch", null, "food"),
            Recipe("Coleslaw Seafood", null, "food"),
            Recipe("Coleslaw Pupusa", null, "food"),

            //todo change small and large for ounces
            Recipe("Shrimp Small", null, "food"),
            Recipe("Shrimp Large", null, "food"),
            Recipe("Ground Pork", null, "food"),
            Recipe("Beef Fajita", null, "food"),
            Recipe("Beef Ground", null, "food"),
            Recipe("Chicken Fajita", null, "food"),
            Recipe("Chicken Shredded", null, "food"),
            Recipe("Chicken boiled", null, "food"),
            Recipe("Brisket", null, "food"),
            Recipe("Pork Tenderloin", null, "food"),
            Recipe("Quail", null, "food"),
            Recipe("Snapper", null, "food"),
            Recipe("Tilapia", null, "food"),
            Recipe("Salmon", null, "food"),

            Recipe("Soup Black Bean", null, "food"),
            Recipe("Soup Chicken", null, "food"),
            Recipe("Soup Chicken Tortilla", null, "food"),
            Recipe("Soup Tortilla Chips", null, "food"),
            Recipe("Soup Mariscada", null, "food"),
            //todo add all the ingredients for the mariscada

            Recipe("Salad Chimichurri", null, "food"),
            Recipe("Salad Garden", null, "food"),
            Recipe("Salad Tropical", null, "food"),

            Recipe("Dressing Honey Balsamic", null, "food"),
            Recipe("Dressing Italian Vinagrette", null, "food"),
            Recipe("Dressing Ranch", null, "food"),
            Recipe("Dressing Chipotle Ranch", null, "food"),
            Recipe("Dressing Balsamic Vinagrette", null, "food"),
            Recipe("Dressing Chimichurri", null, "food"),

            Recipe("Sauce Table Red", null, "food"),
            Recipe("Sauce Green Tomatillo", null, "food"),
            Recipe("Sauce Red Chile Ancho", null, "food"),
            Recipe("Sauce Sour Cream", null, "food"),
            Recipe("Sauce Chili", null, "food"),
            Recipe("Sauce Cheese", null, "food"),
            Recipe("Sauce Tairobi", null, "food"),
            Recipe("Sauce Macha", null, "food"),
            Recipe("Sauce Green Serrano", null, "food"),
            Recipe("Sauce Mole", null, "food"),
            Recipe("Sauce Shrimp Cocktail", null, "food"),
            Recipe("Sauce Garlic", null, "food"),
            Recipe("Sauce Chocolate", null, "food"),
            Recipe("Sauce Caramel", null, "food"),
            Recipe("Sauce Milk", null, "food"),
            Recipe("Sauce Ketchup", null, "food"),
            Recipe("Sauce Honey Orange", null, "food"),

            Recipe("A la Mexicana", null, "food"),
            Recipe("Con Champinones", null, "food"),
            Recipe("Pescado a la Gloria", null, "food"),
            Recipe("Blackened Snapper", null, "food"),
            Recipe("Pescado Acajutla", null, "food"),
            Recipe("Shrimp Garlic", null, "food"),
            Recipe("Quesadilla", null, "food"),
            Recipe("Flauta", null, "food"),
            Recipe("Chile Relleno", null, "food"),
            Recipe("Chimichanga", null, "food"),
            Recipe("Burrito Adult", null, "food"),
            Recipe("Burrito Kid", null, "food"),
            Recipe("Enchilada", null, "food"),
            Recipe("Flan", null, "food"),
            Recipe("Churro", null, "food"),


            //todo add brunch
        )

        gloriasRecipes.map {
            saveRecipeWithIngredients(
                RecipeWithIngredients(
                    it,
                    arrayListOf(),
                    arrayListOf(),
                    arrayListOf()
                )
            )
        }

    }

    fun initGloriasIngredients() {
        val gloriasIngredients = arrayListOf(
            //veggies
            Ingredient("Tomato Cherry", null,null),
            Ingredient("Tomato Roma", null,null),
            Ingredient("Tomato Globe", null,null),
            Ingredient("Onion White", null,null),
            Ingredient("Onion Red", null,null),
            Ingredient("Avocado", null,null),
            Ingredient("Spinach", null,null),
            Ingredient("Mushroom", null,null),
            Ingredient("Green Peas", null,null),
            Ingredient("Brocolli", null,null),
            Ingredient("Green Beans", null,null),
            Ingredient("Mint", null,null),
            Ingredient("White Wine", null,null),
            Ingredient("Vinegar", null,null),
            Ingredient("Beans Pinto", null,null),
            Ingredient("Beans Black", null,null),
            Ingredient("Rice Long White", null,null),
            Ingredient("Snap Peas", null,null),
            Ingredient("Potato Fries", null,null),
            Ingredient("Potato", null,null),
            Ingredient("Rosemary", null,null),
            Ingredient("Grape", null,null),
            Ingredient("Melon", null,null),
            Ingredient("Cole", null,null),
            Ingredient("Chili Flakes", null,null),
            Ingredient("Meat Tenderizer", null,null),
            Ingredient("Pepper Bell Red", null,null),
            Ingredient("Pepper Bell Orange", null,null),
            Ingredient("Pepper Bell Yellow", null,null),
            Ingredient("Pepper Bell Green", null,null),
            Ingredient("Pepper Chipotle", null,null),
            Ingredient("Pepper Guajillo", null,null),
            Ingredient("Pepper Poblano", null,null),
            Ingredient("Pepper Jalapeno", null,null),
            Ingredient("Pepper Poblano", null,null),
            Ingredient("Pepper Ancho", null,null),
            Ingredient("Tomatillo", null,null),
            Ingredient("Ketchup", null,null),
            Ingredient("Churro", null,null),
            //spices
            Ingredient("Salt Iodized", null,null),
            Ingredient("Salt Kosher", null,null),
            Ingredient("Pepper Ground", null,null),
            Ingredient("Pepper Whole", null,null),
            Ingredient("Cumin", null,null),
            Ingredient("Garlic Whole", null,null),
            Ingredient("Garlic Powder", null,null),
            Ingredient("Sugar", null,null),
            Ingredient("Paprika", null,null),
            Ingredient("Bay leaf", null,null),
            Ingredient("Chicken Base", null,null),
            Ingredient("Oil", null,null),
            //todo add more oils
            Ingredient("Butter", null,null),
            Ingredient("Cilantro", null,null),
            Ingredient("Parsley", null,null),
            Ingredient("Jicama", null,null),


            //todo change small/large to oz
            Ingredient("Flour", null,null),
            Ingredient("Chicken Breast", null,null),
            Ingredient("Chicken Thigh", null,null),
            Ingredient("Chicken Leg", null,null),
            Ingredient("Beef Ground", null,null),
            Ingredient("Beef Skirt", null,null),
            Ingredient("Pork Tenderloin", null,null),
            Ingredient("Scallops", null,null),
            Ingredient("Tilapia", null,null),
            Ingredient("Salmon", null,null),
            Ingredient("Red Snapper", null,null),
            Ingredient("Sirloin", null,null),
            Ingredient("Ribeye", null,null),
            Ingredient("Shrimp Small", null,null),
            Ingredient("Shrimp Large", null,null),
            Ingredient("Cheese Cheddar", null,null),
            Ingredient("Cheese Monterrey Jack", null,null),
            Ingredient("Cheese Feta", null,null),
            Ingredient("Cheese Fresco", null,null),
            Ingredient("Cheese flameado 1", null,null),
            Ingredient("Cheese flameado 2", null,null),
            Ingredient("Pumpkin Seeds", null,null),
            Ingredient("Tortilla Corn", null,null),
            Ingredient("Tortilla Corn Small", null,null),
            Ingredient("Tortilla Corn for flauta", null,null),
            Ingredient("Tortilla Corn for Chips", null,null),
            Ingredient("Tortilla Flour", null,null),
            Ingredient("Plantain", null,null),
            Ingredient("Banana", null,null),
            Ingredient("Pinapple", null,null),
            Ingredient("Mandarin", null,null),
            Ingredient("Mango", null,null),
            Ingredient("Strawberry", null,null),
            Ingredient("Lettuce Romain", null,null),
            Ingredient("Lettuce Mix Spring", null,null),
            Ingredient("Carrot", null,null),
            Ingredient("Corn", null,null),


            Ingredient("Sour Cream", null,null),
            Ingredient("Chocolate Black", null,null),
            Ingredient("Honey", null,null),


            //todo add brunch
        )

        saveIngredients(gloriasIngredients)
    }
}
