package com.barryalan.winetraining.data

import androidx.room.*
import com.barryalan.winetraining.model.customer.Customer
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.Job
import com.barryalan.winetraining.model.employee.reference.EmployeeJobRef
import com.barryalan.winetraining.model.employee.reference.ServerSectionRef
import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.*
import com.barryalan.winetraining.model.floor.refrerence.FloorSectionRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableRef
import com.barryalan.winetraining.model.floor.refrerence.FloorTableTypeRef
import com.barryalan.winetraining.model.floor.refrerence.SectionTableRef
import com.barryalan.winetraining.model.floor.with.FloorWithTableTypes
import com.barryalan.winetraining.model.floor.with.FloorWithTablesAndSections
import com.barryalan.winetraining.model.floor.with.FloorWithTablesTableTypes
import com.barryalan.winetraining.model.menu.*
import com.barryalan.winetraining.model.menu.reference.*
import com.barryalan.winetraining.model.menu.with.IngredientWithRecipes
import com.barryalan.winetraining.model.menu.with.MenuItemWithRecipes
import com.barryalan.winetraining.model.menu.with.MenuWithMenuItems
import com.barryalan.winetraining.model.menu.with.RecipeWithIngredients
import java.util.*

@Dao
interface AppDao {

    //Customer
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCustomer(newCustomer: Customer): Long

    @Query("SELECT * FROM Customer")
    fun getAllCustomers(): List<Customer>

    //Employee======================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(employee: Employee): Long

    @Insert
    suspend fun insertAllEmployees(employees: List<Employee>): List<Long>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateEmployee(employee: Employee): Int


    @Query("SELECT * FROM Employee WHERE employeeID = :ID")
    suspend fun getEmployeeByID(ID: Long): Employee

    @Query("SELECT * FROM Employee")
    fun getAllEmployeeWithJobs(): List<EmployeeWithJobs>

    @Query("DELETE FROM Employee Where employeeID = :employeeID")
    suspend fun deleteEmployee(employeeID: Long)


    @Transaction
    suspend fun insertEmployeeWithRefs(employee: EmployeeWithJobs) {
        val employeeID = insertEmployee(employee.employee)

        employee.jobs.map {
            insertEmployeeJobRef(EmployeeJobRef(employeeID, it.name))
        }
    }

    @Transaction
    suspend fun updateEmployeeWithRefs(
        newEmployee: EmployeeWithJobs,
        oldEmployee: EmployeeWithJobs
    ) {

        updateEmployee(newEmployee.employee)

        //jobs to add
        newEmployee.jobs.filter { !oldEmployee.jobs.contains(it) }.map {
            insertEmployeeJobRef(
                EmployeeJobRef(newEmployee.employee.id, it.name)
            )
        }

        //jobs to remove
        oldEmployee.jobs.filter { !newEmployee.jobs.contains(it) }.map {
            deleteEmployeeJobRef(
                newEmployee.employee.id, it.name
            )
        }
    }

    @Transaction
    suspend fun deleteEmployeeWithRefs(employeeID: Long) {
        deleteEmployee(employeeID)
        deleteEmployeeWithRefs(employeeID)
    }

    @Transaction
    suspend fun getTablesServerIDFromTableID(tableID: Long, numberOfServers: Int): Long {
        val sectionID: Long? = getTablesSection(tableID, numberOfServers)

        if (sectionID != null && sectionID != -1L) {
            return getServerIDWithThisSectionID(sectionID)
        } else {
            return -1L
        }

    }

    @Query("SELECT employeeID From EMPLOYEE WHERE name = :employeeName")
    fun getEmployeeIDByName(employeeName: String): Long

    @Query("SELECT * FROM EMPLOYEE WHERE clockInID = :clockInID")
    fun clockInUser(clockInID: Long): Employee

    @Query("SELECT * FROM EMPLOYEE WHERE clockInID = :clockInId")
    fun getEmployeeWithJobs(clockInId: Long): EmployeeWithJobs


    //Jobs==========================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJob(job: Job): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllJobs(jobs: List<Job>): List<Long>

    //Refs==========================================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployeeJobRef(employeeJobRef: EmployeeJobRef): Long

    @Query("DELETE FROM employeeJobRef Where employeeID = :employeeID AND jobName = :jobName")
    suspend fun deleteEmployeeJobRef(employeeID: Long, jobName: String)

    @Transaction
    suspend fun getAllServers(): List<Employee> {
        val serverIDs = getAllServerIDsFromEmployeeRefs()

        val servers = arrayListOf<Employee>()
        serverIDs.map { id ->
            servers.add(getEmployeeByID(id))
        }
        return servers
    }

    @Query("SELECT employeeID From ServerSectionRef WHERE sectionID = :sectionID")
    suspend fun getServerIDWithThisSectionID(sectionID: Long): Long

    @Query("SELECT employeeID FROM EmployeeJobRef WHERE jobName = 'server'")
    suspend fun getAllServerIDsFromEmployeeRefs(): List<Long>


    @Query("SELECT * FROM Employee")
    fun getAllServersWithSections(): List<ServerWithSection>

    //Floor dao ====================================================================================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloor(floor: Floor): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloorTableTypeRef(floorTableTypeRef: FloorTableTypeRef): Long

    @Update
    suspend fun updateFloor(floor: Floor): Int

    @Query("SELECT * FROM FLOOR")
    suspend fun getAllFloors(): List<Floor>

    @Query("DELETE FROM Floor where floorID = :floorID")
    suspend fun deleteFloor(floorID: Long)

    @Transaction
    suspend fun insertFloorWithTableType(floorWithTableTypes: FloorWithTableTypes) {

        floorWithTableTypes.floor.name = floorWithTableTypes.floor.name.toLowerCase(Locale.ROOT)

        var floorId = insertFloor(floorWithTableTypes.floor)

        var count = 2
        val name = floorWithTableTypes.floor.name
        while (floorId == -1L) {
            floorWithTableTypes.floor.name = (name.plus(count)).toLowerCase(Locale.ROOT)

            floorId = insertFloor(floorWithTableTypes.floor)
            count++
        }


        floorWithTableTypes.allTableTypes.map {
            val tableTypeID = insertTableType(it)

            insertFloorTableTypeRef(
                FloorTableTypeRef(floorId, tableTypeID)
            )
        }

    }


    @Transaction
    suspend fun updateFloorWithTableType(
        newFloor: FloorWithTableTypes,
        oldFloor: FloorWithTableTypes
    ) {
        newFloor.floor.id = oldFloor.floor.id
        newFloor.floor.name = newFloor.floor.name.toLowerCase(Locale.ROOT)
        updateFloor(newFloor.floor)

        val tableTypesToAdd = newFloor.allTableTypes.filter { !oldFloor.allTableTypes.contains(it) }
        val tablesTypesToDelete =
            oldFloor.allTableTypes.filter { !newFloor.allTableTypes.contains(it) }

        tablesTypesToDelete.map {
            deleteTableType(it.id)
            deleteFloorTableTypeRef(it.id)
        }

        tableTypesToAdd.map {
            val tableTypeID = insertTableType(it)
            insertFloorTableTypeRef(FloorTableTypeRef(oldFloor.floor.id, tableTypeID))
        }


    }

    @Query("SELECT * FROM Floor WHERE floorID = :floorID")
    suspend fun getFloorWithTableType(floorID: Long): FloorWithTableTypes


    @Transaction
    suspend fun deleteFloorAndAllRefs(floorID: Long) {
        deleteFloor(floorID)

        //delete all other refs
    }


    //Table Type dao ===============================================================================
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTableType(tableType: TableType): Long


    @Query("DELETE FROM TableType where tableTypeID = :tableTypeID")
    suspend fun deleteTableType(tableTypeID: Long)

    @Query("DELETE FROM FloorTableTypeRef where tableTypeID = :tableTypeID")
    suspend fun deleteFloorTableTypeRef(tableTypeID: Long)

    @Query("SELECT * FROM Floor WHERE floorID = :floorID")

    suspend fun getFloorWithTablesTableTypes(floorID: Long): FloorWithTablesTableTypes

    //Table dao =====================================================================================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTable(table: Table): Long

    @Update
    suspend fun updateTable(selectedTable: Table): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloorTableRef(floorTableRef: FloorTableRef): Long

    @Transaction
    suspend fun insertTableWithRef(floorID: Long, table: Table) {
        val tableID = insertTable(table)

        insertFloorTableRef(
            FloorTableRef(
                floorID,
                tableID
            )
        )
    }

    @Query("DELETE FROM `TABLE` where tableID = :tableID")
    suspend fun deleteTable(tableID: Long)


    @Query("DELETE FROM FloorTableRef where tableID = :tableID")
    suspend fun deleteFloorTableRef(tableID: Long)


    @Transaction
    suspend fun deleteTableWithRef(tableID: Long) {
        deleteTable(tableID)
        deleteFloorTableRef(tableID)
    }

    //Sections Dao==================================================================================

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSection(section: Section): Long

    @Query("DELETE FROM Section where sectionID = :sectionID")
    suspend fun deleteSection(sectionID: Long)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSectionTableRef(sectionTableRef: SectionTableRef): Long

    @Transaction
    suspend fun insertSectionTableRefAndDeleteOthers(
        sectionTableRef: SectionTableRef,
        numberOfServers: Int
    ) {

        val itemAlreadyExisted = insertSectionTableRef(sectionTableRef)

        if (itemAlreadyExisted == -1L) {
            deleteSectionTableRef(sectionTableRef)
        }

        deleteSectionTableRef(sectionTableRef.tableID, numberOfServers, sectionTableRef.sectionID)

    }

    @Query("DELETE FROM SectionTableRef WHERE tableID = :tableID AND numberOfServers = :numberOfServers AND sectionID !=:sectionID ")
    suspend fun deleteSectionTableRef(tableID: Long, numberOfServers: Int, sectionID: Long)


    @Delete
    suspend fun deleteSectionTableRef(sectionTableRef: SectionTableRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFloorSectionRef(floorSectionRef: FloorSectionRef): Long

    @Delete
    suspend fun deleteFloorSectionRef(floorSectionRef: FloorSectionRef)


    @Query("SELECT * FROM Floor WHERE floorID = :floorID")
    suspend fun getFloorWithTablesAndSections(floorID: Long): FloorWithTablesAndSections

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSectionColor(sectionColor: SectionColor)


    @Query("SELECT * FROM SectionColor")
    suspend fun getAllSectionColors(): List<SectionColor>


    @Query("SELECT sectionID FROM SectionTableRef WHERE tableID = :tableID AND numberOfServers = :numberOfServers")
    suspend fun getTablesSection(tableID: Long, numberOfServers: Int): Long

    @Transaction
    suspend fun insertSectionWithRef(section: Section, floorID: Long) {
        val sectionId = insertSection(section)

        if (sectionId != -1L) {

            insertFloorSectionRef(FloorSectionRef(floorID, sectionId))
        }

    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertServerSectionRef(serverSectionRef: ServerSectionRef)

    @Transaction
    fun insertServerSectionRefDeleteOther(serverSectionRef: ServerSectionRef) {
        deleteAllFloorServerSectionRefs(serverSectionRef.floorID)
        insertServerSectionRef(serverSectionRef)
    }


    @Query("DELETE FROM ServerSectionRef WHERE floorID = :floorID")
    fun deleteAllFloorServerSectionRefs(floorID: Long)

    @Query("DELETE FROM ServerSectionRef WHERE floorID = :floorId AND employeeID = :employeeID")
    fun deleteAllServerSectionRefs(floorId: Long, employeeID: Long)


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

    @Query("SELECT * FROM Menu")
    suspend fun getAllMenusWithMenuItems(): List<MenuWithMenuItems>

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
    suspend fun insertMenuItemInstructionRef(menuItemInstructionRef: MenuItemInstructionRef): Long


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