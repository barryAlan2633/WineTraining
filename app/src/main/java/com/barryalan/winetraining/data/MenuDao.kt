package com.barryalan.winetraining.data

import androidx.room.*
import com.barryalan.winetraining.model.menu.*
import com.barryalan.winetraining.model.menu.reference.*
import com.barryalan.winetraining.model.menu.with.MenuItemWithRecipes
import com.barryalan.winetraining.model.menu.with.MenuWithMenuItems
import java.util.*
import kotlin.collections.ArrayList

@Dao
interface MenuDao {
    //MenuItemDao=====================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMenuItem(menuItem: MenuItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMenuItems(menuItems: List<MenuItem>): List<Long>

    @Query("SELECT * FROM MenuItem WHERE menuItemID = :ID")
    suspend fun getMenuItem(ID: Long): MenuItem

    @Query("SELECT menuItemID FROM MenuItem WHERE name = :name")
    suspend fun getMenuItemID(name: String): Long

    @Query("SELECT * FROM MenuItem")
    suspend fun getAllMenuItems(): List<MenuItem>

    @Transaction
    suspend fun getAllMenuItems(IDs: List<Long>): List<MenuItem> {
        val menuItemList: MutableList<MenuItem> = mutableListOf()

        IDs.map {
            menuItemList.add(getMenuItem(it))
        }

        return menuItemList
    }

    @Query("SELECT * FROM MenuItem")
    fun getAllMenuItemWithRecipes(): List<MenuItemWithRecipes>

    @Query("SELECT * FROM MenuItem WHERE menuItemID = :menuItemID")
    fun getMenuItemWithRecipes(menuItemID: Long): MenuItemWithRecipes?

    @Delete
    suspend fun deleteMenuItem(menuItem: MenuItem)

    @Query("DELETE FROM MenuItem Where menuItemID = :menuItemID")
    suspend fun deleteMenuItem(menuItemID: Long)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateMenuItem(menuItem: MenuItem): Int

    @Query("DELETE FROM MenuItem")
    suspend fun nukeMenuItemTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItemRecipeRef(menuItemRecipeRef: MenuItemRecipeRef)

    @Query("DELETE FROM MenuItemRecipeRef WHERE amountID = :amountID")
    suspend fun deleteMenuItemRefByAmountID(amountID: Long)

    @Transaction
    suspend fun deleteMenuItemWithPrices(menuItemID: Long, menuID: Long) {
        val menuItemRef = getMenuItemRef(menuItemID)
        deleteMenuRefsWithMenuItemID(menuItemID, menuID)
        deletePrice(menuItemRef.priceID)
    }

    @Transaction
    suspend fun insertMenuItemWithRecipes(
        menuItem: MenuItem,
        recipeList: ArrayList<Recipe>,
        amountsList: List<Amount>,
        instructionList: List<Instruction>
    ) {

        var menuItemCalories: Int? = 0

        //normalize name
        menuItem.name = menuItem.name.toLowerCase(Locale.ROOT)

        //save menu item and get id
        var menuItemID = insertMenuItem(menuItem)

        //if the menu item was not inserted properly due to name conflict add a number and retry
        var count = 2
        while (menuItemID == -1L) {
            menuItem.name = menuItem.name + count
            menuItemID = insertMenuItem(menuItem)
            count++
        }

        //insert all amounts
        val amountsIDs = insertAllAmounts(amountsList)


        //insert Refs
        recipeList.mapIndexed { index, recipe ->
            //save ref
            insertMenuItemRecipeRef(
                MenuItemRecipeRef(
                    menuItemID = menuItemID,
                    recipeID = recipe.id,
                    amountID = amountsIDs[index]
                )
            )

            if (recipe.calories != null) {
                menuItemCalories = menuItemCalories?.plus(recipe.calories!!)
            } else {
                menuItemCalories = null
            }
        }


        //update to add calories to database
        menuItem.calories = menuItemCalories
        updateMenuItem(menuItem)


        //insert all instructions
        val instructionsIDs = insertAllInstructions(instructionList)

        //insert all instruction refs
        instructionsIDs.map {
            insertMenuItemInstructionRef(
                MenuItemInstructionRef(
                    menuItemID,
                    it
                )
            )
        }
    }

    //todo if i update an ingredients calories then don't update a recipe at all it will never show the
    //todo correct calorie sum and neither will the menu item
    //todo display of calories shows 0 when it had a previous un-null amount then the recipes where removed
    suspend fun updateMenuItemWithRecipes(
        updatedMenuItem: MenuItem,
        updatedRecipeList: ArrayList<Recipe>,
        updatedAmountsList: List<Amount>,
        updatedInstructionList: List<Instruction>,
        toUpdate: MenuItemWithRecipes

    ) {
        var menuItemCalories: Int? = 0

        //Give the new item the old ones id
        updatedMenuItem.id = toUpdate.menuItem.id

        // normalize name to avoid duplication
        updatedMenuItem.name = updatedMenuItem.name.toLowerCase(Locale.ROOT)


        //items to be insert these don't have IDs set
        val recipesToInsert = arrayListOf<Recipe>()
        val amountsToInsert = arrayListOf<Amount>()
        updatedRecipeList.mapIndexed { index, recipe ->
            if (!toUpdate.recipes.contains(recipe)) {
                recipesToInsert.add(recipe)
                amountsToInsert.add(updatedAmountsList[index])


            }

            //get the total calories for this recipe
            if (recipe.calories != null) {
                menuItemCalories = menuItemCalories?.plus(recipe.calories!!)
            } else {
                menuItemCalories = null
            }
        }

        //update calories
        updatedMenuItem.calories = menuItemCalories

        // update menuItem
        var updatedRows = updateMenuItem(updatedMenuItem)

        //if the item was not updated properly due to name conflict add a number and try again
        var count = 2
        while (updatedRows == 0) {
            updatedMenuItem.name = updatedMenuItem.name + count
            updatedRows = updateMenuItem(updatedMenuItem)
            count++
        }


        //items to be deleted these have IDs set already
        val amountsToDelete = arrayListOf<Amount>()
        toUpdate.recipes.mapIndexed { index, recipe ->
            if (!updatedRecipeList.contains(recipe)) {
                amountsToDelete.add(toUpdate.amounts[index])
            }
        }

        //Retrieve IDs of recipes
        val finalRecipesIDs: MutableList<Long> = mutableListOf()
        recipesToInsert.map { finalRecipesIDs.add(getRecipeByName(it.name)!!.id) }

        //insert all new amounts
        val amountsIDs = insertAllAmounts(amountsToInsert)

        //insert all new references
        finalRecipesIDs.mapIndexed { index, recipeID ->
            val reference = MenuItemRecipeRef(
                toUpdate.menuItem.id,
                recipeID,
                amountsIDs[index]
            )

            insertMenuItemRecipeRef(reference)
        }

        //delete All unwanted references
        amountsToDelete.map {
            deleteMenuItemRefByAmountID(it.id)
        }

        // delete unwanted amounts
        deleteAllAmounts(amountsToDelete)


        //insert all instructions
        val instructionsIDs = insertAllInstructions(updatedInstructionList)

        instructionsIDs.mapIndexed { index, instructionID ->
            if (instructionID == -1L) {
                updateInstruction(updatedInstructionList[index])
            } else {
                insertMenuItemInstructionRef(
                    MenuItemInstructionRef(
                        updatedMenuItem.id,
                        instructionID
                    )
                )
            }

        }
    }


    @Query("DELETE FROM MenuItemRecipeRef WHERE MenuItemID = :menuItemID")
    fun deleteMenuItemRefs(menuItemID: Long)

    @Transaction
    suspend fun deleteMenuItemWithRefs(menuItemID: Long) {
        deleteMenuItemRefs(menuItemID)
        deleteMenuItem(menuItemID)
    }

    //MenuDao=======================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMenu(menu: Menu): Long

    @Query("SELECT * FROM Menu")
    suspend fun getAllMenus(): List<Menu>

    @Query("SELECT * FROM Menu WHERE menuID = :menuID")
    fun getMenuWithMenuItems(menuID: Long): MenuWithMenuItems?

    @Delete
    suspend fun deleteMenu(menu: Menu)

    @Query("DELETE FROM Menu Where menuID = :menuID")
    suspend fun deleteMenu(menuID: Long)

    @Query("DELETE FROM MenuMenuItemRef WHERE MenuID = :menuID")
    fun deleteMenuRefsWithMenuID(menuID: Long)

    @Query("DELETE FROM MenuMenuItemRef WHERE MenuItemID = :menuItemID AND MenuID = :menuID")
    fun deleteMenuRefsWithMenuItemID(menuItemID: Long, menuID: Long)

    @Query("DELETE FROM MenuMenuItemRef WHERE priceID = :priceID")
    fun deleteMenuRefsWithPriceID(priceID: Long)

    @Transaction
    suspend fun deleteMenuWithRefs(menuID: Long) {
        deleteMenuRefsWithMenuID(menuID)
        deleteMenu(menuID)
    }

    @Transaction
    suspend fun insertMenuWithMenuItems(
        menu: Menu,
        menuItems: ArrayList<MenuItem>,
        menuItemsPrices: ArrayList<Price>
    ) {

        //normalize recipe name to avoid duplication
        menu.name = menu.name.toLowerCase(Locale.ROOT)

        //insert menu item and get id
        var menuID = insertMenu(menu)

        //if the menu item was not inserted properly due to name conflict add a number and retry
        var count = 2
        while (menuID == -1L) {
            menu.name = menu.name + count
            menuID = insertMenu(menu)
            count++
        }


        //get menu items IDs
        val menuItemsIDs = menuItems.map { menuItem ->
            getMenuItemID(menuItem.name)
        }


        //insert menu item prices and get ids
        val menuItemsPricesIDs = insertAllPrices(menuItemsPrices)


        //insert Refs
        menuItemsIDs.mapIndexed { index, menuItemID ->
            insertMenuMenuItemRef(
                MenuMenuItemRef(
                    menuID,
                    menuItemID,
                    menuItemsPricesIDs[index]
                )
            )
        }


    }

    @Transaction
    suspend fun updateMenuWithMenuItems(
        updatedMenu: Menu,
        updatedMenuItems: ArrayList<MenuItem>,
        toUpdate: MenuWithMenuItems,
        updatedMenuItemsPrices: ArrayList<Price>
    ) {

        //Give the new menu item the old ones id
        updatedMenu.id = toUpdate.menu.id

        //normalize menu name to avoid duplication
        updatedMenu.name = updatedMenu.name.toLowerCase(Locale.ROOT)

        //update menu
        var updatedRows = updateMenu(updatedMenu)

        //if the item was not updated properly due to name conflict add a number and try again
        var count = 2
        while (updatedRows == 0) {
            updatedMenu.name = updatedMenu.name + count
            updatedRows = updateMenu(updatedMenu)
            count++
        }


        //get items to insert
        val menuItemsToInsert = arrayListOf<MenuItem>()
        val menuItemsPricesToInsert = arrayListOf<Price>()
        updatedMenuItems.mapIndexed { index, menuItem ->
            if (!toUpdate.menuItems.contains(menuItem)) {
                menuItemsToInsert.add(menuItem)
                menuItemsPricesToInsert.add(updatedMenuItemsPrices[index])
            }
        }

        //get menu items IDs
        val menuItemsIDs = menuItemsToInsert.map { menuItem ->
            getMenuItemID(menuItem.name)
        }
        //insert menu item prices and get ids
        val menuItemsPricesIDs = insertAllPrices(menuItemsPricesToInsert)

        //insert Refs
        menuItemsIDs.mapIndexed { index, menuItemID ->
            insertMenuMenuItemRef(
                MenuMenuItemRef(
                    updatedMenu.id,
                    menuItemID,
                    menuItemsPricesIDs[index]
                )
            )
        }

        //get items to delete
        val menuItemsPricesToDelete =
            toUpdate.menuItemsPrices.filter { !updatedMenuItemsPrices.contains(it) }

        //delete items that we no longer need
        menuItemsPricesToDelete.map {
            deleteMenuRefsWithPriceID(it.id)
            deletePrice(it.id)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuMenuItemRef(menuRecipeRef: MenuMenuItemRef)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateMenu(menu: Menu): Int

    @Query("DELETE FROM Menu")
    suspend fun nukeMenuTable()

    //AmountDao=====================================================================================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAmounts(amounts: List<Amount>): List<Long>

    @Delete
    suspend fun deleteAllAmounts(amount: List<Amount>)

    //RecipeDAO=====================================================================================
    @Query("SELECT * FROM Recipe WHERE name= :recipeName")
    suspend fun getRecipeByName(recipeName: String): Recipe?

    //PriceDAO======================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrice(price: Price): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPrices(prices: List<Price>): List<Long>

    @Update
    suspend fun updatePrice(price: Price): Int

    @Query("SELECT * FROM Price WHERE priceID = :priceID")
    suspend fun getPrice(priceID: Long): Price

    @Query("DELETE FROM Price WHERE priceID = :priceID")
    suspend fun deletePrice(priceID: Long)

    @Query("SELECT * FROM MenuMenuItemRef WHERE menuItemID = :menuItemID")
    suspend fun getMenuItemRef(menuItemID: Long): MenuMenuItemRef

    //InstructionsDAO===============================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllInstructions(instructionList: List<Instruction>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMenuItemInstructionRef(menuItemInstructionRef: MenuItemInstructionRef): Long

    @Update
    suspend fun updateInstruction(instruction: Instruction): Int


    //IngredientDao=================================================================================
    @Query("SELECT ingredientID FROM Ingredient WHERE name = :name")
    suspend fun getIngredientID(name: String): Long

    //RandomDAO======================================================================================

    @Transaction
    suspend fun getRecipeIDFromIngredientRefContainingIngredient(
        ingredientNameFilterList: List<String>,
        allIngredientIDs: List<Long>?
    ): List<Long> {

        if (ingredientNameFilterList.isNotEmpty()) {
            val ingredientIds = arrayListOf<Long>()
            val ingredientIdsToRemove = arrayListOf<Long>()


            ingredientNameFilterList.map { ingredientName ->
                val ingredientID = getIngredientID(
                    ingredientName.removePrefix("not").trim().toLowerCase(Locale.ROOT)
                )

                if (ingredientName.contains("not")) {
                    ingredientIdsToRemove.add(ingredientID)
                    allIngredientIDs?.let {
                        ingredientIds.addAll(it)
                    }
                } else {
                    ingredientIds.add(ingredientID)
                }
            }

            val recipeIds = arrayListOf<Long>()
            ingredientIds.distinct().map { ingredientId ->
                recipeIds.addAll(getRecipeIDFromIngredientRefContainingIngredient(ingredientId))
            }


            val recipeIdsToRemove = arrayListOf<Long>()
            ingredientIdsToRemove.distinct().map { ingredientIdToRemove ->
                recipeIdsToRemove.addAll(
                    getRecipeIDFromIngredientRefContainingIngredient(
                        ingredientIdToRemove
                    )
                )
            }


            return recipeIds.distinct().filter { !recipeIdsToRemove.contains(it) }
        }

        return arrayListOf()

    }

    @Query("SELECT recipeID FROM RecipeIngredientRef WHERE ingredientID = :ingredientId")
    fun getRecipeIDFromIngredientRefContainingIngredient(ingredientId: Long): List<Long>
}