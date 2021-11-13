package com.barryalan.winetraining.data

import androidx.room.*
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.Instruction
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.with.IngredientWithRecipes
import com.barryalan.winetraining.model.menu.reference.RecipeIngredientRef
import com.barryalan.winetraining.model.menu.reference.RecipeInstructionRef
import com.barryalan.winetraining.model.menu.with.RecipeWithIngredients
import java.util.*

@Dao
interface FoodDao {

    //RecipeDao=====================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Insert
    suspend fun insertAllRecipes(recipes: List<Recipe>): List<Long>

    @Query("SELECT * FROM Recipe WHERE recipeID = :ID")
    suspend fun getRecipe(ID: Long): Recipe

    @Query("SELECT recipeID FROM Recipe WHERE name = :name")
    suspend fun getRecipeID(name: String): Long

    @Query("SELECT * FROM Recipe")
    suspend fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM Recipe")
    suspend fun getAllRecipesWithIngredients(): MutableList<RecipeWithIngredients>

    @Transaction
    suspend fun getAllRecipes(IDs: List<Long>): List<Recipe> {
        val recipeList: MutableList<Recipe> = mutableListOf()
        for (id in IDs) {
            recipeList.add(getRecipe(id))
        }

        return recipeList
    }

    @Query("SELECT * FROM Recipe WHERE name = :recipeName")
    suspend fun getRecipeByName(recipeName: String): Recipe?

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM Recipe Where recipeID = :recipeID")
    suspend fun deleteRecipe(recipeID: Long)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateRecipe(recipe: Recipe): Int

    @Query("DELETE FROM Recipe")
    suspend fun nukeRecipeTable()


    //IngredientDao=================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIngredients(ingredients: List<Ingredient>): List<Long>

    @Query("SELECT * FROM Ingredient")
    suspend fun getAllIngredients(): List<Ingredient>

    @Query("SELECT * FROM Ingredient WHERE ingredientID = :ID")
    suspend fun getIngredient(ID: Long): Ingredient

    @Query("SELECT ingredientID FROM Ingredient WHERE name = :name")
    suspend fun getIngredientID(name: String): Long

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("DELETE FROM Ingredient WHERE ingredientID = :ingredientID")
    suspend fun deleteIngredient(ingredientID: Long)

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Query("DELETE FROM Ingredient")
    suspend fun nukeIngredientTable()


    //AmountDao=====================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAmount(amount: Amount): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllAmounts(amounts: List<Amount>): List<Long>

    @Query("SELECT * FROM Amount")
    suspend fun getAllAmounts(): List<Amount>

    @Query("SELECT * FROM Amount WHERE amountID = :ID")
    suspend fun getAmount(ID: Long): Amount

    @Delete
    suspend fun deleteAmount(amount: Amount)

    @Delete
    suspend fun deleteAllAmounts(amount: List<Amount>)

    @Update
    suspend fun updateAmount(amount: Amount)

    @Query("DELETE FROM Amount")
    suspend fun nukeAmountTable()

    //ReferenceDao==================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeIngredientRef(recipeIngredientRef: RecipeIngredientRef): Long

    @Query("SELECT * FROM RecipeIngredientRef WHERE recipeID = :recipeID")
    suspend fun getRecipeIngredientRef(recipeID: Long): List<RecipeIngredientRef>?

    @Query("DELETE FROM RecipeIngredientRef WHERE recipeID = :recipeID")
    suspend fun deleteAllRecipeIngredientRef(recipeID: Long)

    @Query("DELETE FROM RecipeIngredientRef WHERE amountID = :amountID")
    suspend fun deleteRecipeIngredientRef(amountID: Long)

    @Query("DELETE FROM RecipeIngredientRef")
    suspend fun nukeRecipeIngredientRefTable()



    //RecipeWithIngredients CRUD====================================================================
    @Transaction
    @Query("SELECT * FROM Recipe WHERE recipeID = :ID")
    suspend fun getRecipeWithIngredients(ID: Long): RecipeWithIngredients

    @Transaction
    suspend fun insertRecipeWithIngredients(recipeWithIngredientsToInsert: RecipeWithIngredients) {

        var recipeCalories: Int? = 0

        //normalize recipe name to avoid duplication
        recipeWithIngredientsToInsert.recipe.name =
            recipeWithIngredientsToInsert.recipe.name.toLowerCase(Locale.ROOT)

        //normalize all ingredient names to avoid duplication and add up calories
        recipeWithIngredientsToInsert.ingredients.map {
            it.name = it.name.toLowerCase(Locale.ROOT)
            if (it.calories != null) {
                recipeCalories = recipeCalories?.plus(it.calories)
            } else {
                recipeCalories = null
            }
        }

        //set calories
        recipeWithIngredientsToInsert.recipe.calories = recipeCalories

        // insert recipe
        var recipeID = insertRecipe(recipeWithIngredientsToInsert.recipe)


        //if the recipe was not inserted properly due to name conflict add a number and retry
        var count = 2
        val recipeName = recipeWithIngredientsToInsert.recipe.name
        while (recipeID == -1L) {
            count++
            recipeWithIngredientsToInsert.recipe.name = recipeName + count
            recipeID = insertRecipe(recipeWithIngredientsToInsert.recipe)
        }

        //insert all ingredients
        val ingredientsIDs =
            insertAllIngredients(recipeWithIngredientsToInsert.ingredients).toMutableList()


        //Retrieve IDs of items that were not inserted, using name(ID = -1)
        ingredientsIDs.mapIndexed { index, ingredientID ->
            if (ingredientID == -1L) {
                val actualID =
                    getIngredientID(
                        recipeWithIngredientsToInsert.ingredients[index].name.toLowerCase(
                            Locale.ROOT
                        )
                    )

                ingredientsIDs[index] = actualID
            }
        }

        //insert all instructions
        val instructionsIDs = insertAllInstructions(recipeWithIngredientsToInsert.instructions)

        //insert all instruction refs
        instructionsIDs.map {
            insertRecipeInstructionRef(
                RecipeInstructionRef(
                    recipeID,
                    it
                )
            )
        }

        //insert all amounts
        val amountsIDs = insertAllAmounts(recipeWithIngredientsToInsert.amounts)


        //insert all recipe ingredient amount refs
        ingredientsIDs.mapIndexed { index, ingredientID ->
            val reference =
                RecipeIngredientRef(recipeID, ingredientID, amountsIDs[index])

            insertRecipeIngredientRef(reference)
        }


    }

    @Transaction
    suspend fun updateRecipeWithIngredients(
        updated: RecipeWithIngredients,
        toUpdate: RecipeWithIngredients
    ) {
        var recipeCalories: Int? = 0

        //give the updated recipe the ID of the old one thus allowing it to be updated
        updated.recipe.id = toUpdate.recipe.id

        // normalize recipe name to avoid duplication
        updated.recipe.name = updated.recipe.name.toLowerCase(Locale.ROOT)

        //items to be insert these don't have IDs set
        val ingredientsToInsert = arrayListOf<Ingredient>()
        val amountsToInsert = arrayListOf<Amount>()

        updated.ingredients.mapIndexed { index, ingredient ->
            if (!toUpdate.ingredients.contains(ingredient)) {

                //normalize all ingredient names to avoid duplication
                ingredient.name = ingredient.name.toLowerCase(Locale.ROOT)

                ingredientsToInsert.add(ingredient)
                amountsToInsert.add(updated.amounts[index])

                //get the total calories for this recipe
            }

            if (recipeCalories != null && ingredient.calories != null) {
                recipeCalories = recipeCalories?.plus(ingredient.calories)
            } else {
                recipeCalories = null
            }

        }

        //set calories
        updated.recipe.calories = recipeCalories

        // update recipe
        var updatedRows = updateRecipe(updated.recipe)

        //if the recipe was not updated properly due to name conflict add a number and try again
        var count = 2
        val recipeName = updated.recipe.name.toLowerCase(Locale.ROOT)
        while (updatedRows == 0) {
            count++
            updated.recipe.name = recipeName + count
            updatedRows = updateRecipe(updated.recipe)
        }

        //items to be deleted these have IDs set already
        val ingredientsToDelete = arrayListOf<Ingredient>()
        val amountsToDelete = arrayListOf<Amount>()
        toUpdate.ingredients.mapIndexed { index, ingredient ->
            if (!updated.ingredients.contains(ingredient)) {
                ingredientsToDelete.add(ingredient)
                amountsToDelete.add(toUpdate.amounts[index])
            }
        }

        // delete unwanted amounts and refs
        amountsToDelete.map { amount ->
            deleteAmount(amount)
            deleteRecipeIngredientRef(amount.id)
        }

        // insert all ingredients
        val ingredientsIDs = insertAllIngredients(ingredientsToInsert).toMutableList()


        //retrieve IDs of items that were not inserted properly(ID=-1)
        ingredientsIDs.mapIndexed { index, ingredientID ->
            if (ingredientID == -1L) {
                val actualID: Long =
                    getIngredientID(ingredientsToInsert[index].name.toLowerCase(Locale.ROOT))
                ingredientsIDs[index] = actualID
            }
        }


        //insert all new amounts
        val amountsIDs = insertAllAmounts(amountsToInsert)


        //insert all new references
        ingredientsIDs.mapIndexed { index, ingredientID ->
            val reference =
                RecipeIngredientRef(
                    toUpdate.recipe.id,
                    ingredientID,
                    amountsIDs[index]
                )
            insertRecipeIngredientRef(reference)
        }


        //insert all instructions
        val instructionsIDs = insertAllInstructions(updated.instructions)

        instructionsIDs.mapIndexed { index, instructionID ->
            if (instructionID == -1L) {
                updateInstruction(updated.instructions[index])
            } else {
                insertRecipeInstructionRef(
                    RecipeInstructionRef(
                        toUpdate.recipe.id,
                        instructionID
                    )
                )
            }

        }


//TODO there is an issue if the ingredient is already in the list then removed and added back with different amount in one transaction prob same problem in menu item with the price

        //TODO can probably make this perform better if I make a db request to only update the field that changed instead of the whole object
        //TODO can probably write the updating of notes better as well
//            //update the recipe
//        if(updatedRecipeWithIngredients.recipe.name != recipeWithIngredientsToUpdate.recipe.name &&
//            updatedRecipeWithIngredients.recipe.image != recipeWithIngredientsToUpdate.recipe.image){
//            //update whole recipe
//        }else if(updatedRecipeWithIngredients.recipe.name != recipeWithIngredientsToUpdate.recipe.name){
//            //update the recipe name
//
//        }else{
//            //update the recipe image
//        }
//

    }

    @Transaction
    suspend fun deleteRecipeAndAssociations(recipeID: Long) {
        //delete this recipe
        deleteRecipe(recipeID)

        //delete all recipeIngredientsRef that belong to this recipe
        deleteAllRecipeIngredientRef(recipeID)
    }


    //IngredientWithRecipes CRUD====================================================================
    @Transaction
    @Query("SELECT * FROM Ingredient")
    suspend fun getAllIngredientWithRecipes(): List<IngredientWithRecipes>

    //InstructionsDAO======================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInstruction(instruction: Instruction): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeInstructionRef(recipeInstructionRef: RecipeInstructionRef): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllInstructions(instructions: List<Instruction>): List<Long>


    @Update
    suspend fun updateInstruction(instruction: Instruction): Int

    @Query("SELECT * FROM Instruction WHERE instructionID = :instructionID")
    suspend fun getInstruction(instructionID: Long): Instruction

    @Query("DELETE FROM Instruction WHERE instructionID = :instructionID")
    suspend fun deleteInstruction(instructionID: Long)
}