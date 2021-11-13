package com.barryalan.winetraining.ui.menu.recipes

import com.barryalan.winetraining.ui.menu.ingredients.IngredientWithAmountsAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.Instruction
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.model.menu.with.RecipeWithIngredients
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.CameraActivity
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecipeNewEdit : BaseFragment(), BottomSheetIngredientCallback {

    private lateinit var imgRecipe: ImageView
    private lateinit var etRecipeName: TextInputEditText
    private lateinit var rvIngredientList: RecyclerView
    private lateinit var rvInstructionList: RecyclerView
    private lateinit var etNewInstructionText: EditText
    private lateinit var imgNewIngredient: ImageView
    private lateinit var etNewIngredientName: EditText
    private lateinit var etNewIngredientAmount: EditText
    private lateinit var spNewIngredientUnit: Spinner
    private lateinit var btnAddIngredient: MaterialButton
    private lateinit var btnAddInstruction: MaterialButton
    private lateinit var btnUpdateSaveRecipe: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnEditIngredients: MaterialButton
    private lateinit var chipGroup: ChipGroup

    private val ingredientListAdapter = IngredientWithAmountsAdapter(arrayListOf())
    private val instructionListAdapter = RecipeInstructionsListAdapter(arrayListOf())
    private lateinit var viewModel: RecipeNewEditViewModel
    private val bottomSheetIngredientListAdapter = BottomSheetIngredientListAdapter(
        arrayListOf(),
        this,
        arrayListOf(),
        arrayListOf(),
    )

    private var mRecipeToEditUID: Long = -1L
    private var mRecipeImageURIString: String? = null
    private var mIngredientImageURIString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            confirmBackNavigation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_new_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgRecipe = view.findViewById(R.id.img_recipe_recipe_new_edit)
        etRecipeName = view.findViewById(R.id.et_recipe_name_recipe_new_edit)
        rvIngredientList = view.findViewById(R.id.rv_ingredient_list_recipe_new_edit)
        rvInstructionList = view.findViewById(R.id.rv_instruction_list_recipe_new_edit)
        chipGroup = view.findViewById(R.id.chip_group_recipe_new_edit)
        imgNewIngredient = view.findViewById(R.id.img_new_ingredient_recipe_new_edit)
        etNewInstructionText = view.findViewById(R.id.et_new_instruction_text_recipe_new_edit)
        etNewIngredientName = view.findViewById(R.id.et_new_Ingredient_name_recipe_new_edit)
        etNewIngredientAmount = view.findViewById(R.id.et_new_ingredient_amount_recipe_new_edit)
        spNewIngredientUnit = view.findViewById(R.id.sp_new_ingredient_unit_recipe_new_edit)
        btnAddIngredient = view.findViewById(R.id.btn_add_ingredient_recipe_new_edit)
        btnAddInstruction = view.findViewById(R.id.btn_add_instruction_recipe_new_edit)
        btnUpdateSaveRecipe = view.findViewById(R.id.btn_update_save_recipe_recipe_new_edit)
        btnCancel = view.findViewById(R.id.btn_cancel_recipe_new_edit)
        btnEditIngredients = view.findViewById(R.id.btn_edit_ingredients_recipe_new_edit)

        viewModel = ViewModelProvider(this).get(RecipeNewEditViewModel::class.java)
        viewModel.fetchIngredientList()
        arguments?.let {
            mRecipeToEditUID = RecipeNewEditArgs.fromBundle(it).recipeUID
            viewModel.fetchSelectedRecipeWI(mRecipeToEditUID)
        }
        initIngredientRecyclerView()
        initInstructionRecyclerView()
        initUnitSpinner()
        subscribeObservers()

        btnEditIngredients.setOnClickListener {
            openMenuItemsBottomSheet()

        }

        btnAddIngredient.setOnClickListener {
            when {
                etNewIngredientName.text.isEmpty() -> {
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "Ingredient must have a name",
                            UIMessageType.ErrorDialog()
                        )
                    )
                }
                etNewIngredientAmount.text.isEmpty() -> {
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "Ingredient must have an amount",
                            UIMessageType.ErrorDialog()
                        )
                    )
                }
                else -> {
                    //create new object with the input fields
                    val newIngredient =
                        Ingredient(
                            etNewIngredientName.text.toString().trim().capitalize(Locale.ROOT),
                            mIngredientImageURIString,
                            null
                        )
                    val newAmount =
                        Amount(
                            etNewIngredientAmount.text.toString().toFloat(),
                            spNewIngredientUnit.selectedItem.toString()
                        )

                    //add to recyclerview
                    ingredientListAdapter.addIngredientItem(newIngredient, newAmount)

                    //clear fields
                    etNewIngredientName.text.clear()
                    etNewIngredientAmount.text.clear()
                    imgNewIngredient.setImageResource(R.drawable.ic_error_outline_black_24dp)
                    mIngredientImageURIString = null
                }
            }
        }

        btnAddInstruction.setOnClickListener {
            when {
                etNewInstructionText.text.isEmpty() -> {
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "Instruction must have text",
                            UIMessageType.ErrorDialog()
                        )
                    )
                }
                else -> {
                    //create new object with the input fields
                    val newInstruction =
                        Instruction(
                            instructionListAdapter.itemCount + 1,
                            etNewInstructionText.text.toString().trim().capitalize(Locale.ROOT)
                        )

                    //add to recyclerview
                    instructionListAdapter.addInstruction(newInstruction)

                    //clear fields
                    etNewInstructionText.text.clear()
                }
            }
        }

        btnCancel.setOnClickListener {
            confirmBackNavigation()
        }

        btnUpdateSaveRecipe.setOnClickListener {

            //Check to make sure there is a name on the recipe
            if (etRecipeName.text.toString() == "") {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "Recipe must have a name",
                        UIMessageType.ErrorDialog()
                    )
                )
            } else if (chipGroup.checkedChipId == View.NO_ID) {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "Recipe must have a type. (Drink or Food)",
                        UIMessageType.ErrorDialog()
                    )
                )
            } else {
                if (mRecipeToEditUID == -1L) { //User is in this fragment to make a new recipe
                    if (etRecipeName.text.toString() == "recipes") {
                        viewModel.initGloriasRecipes()
                    } else if (etRecipeName.text.toString() == "ingredients") {
                        viewModel.initGloriasIngredients()
                    } else if (etRecipeName.text.toString() == "clear recipes") {
                        viewModel.nukeRecipes()
                    } else if (etRecipeName.text.toString() == "clear ingredients") {
                        viewModel.nukeIngredients()
                    } else {
                        saveNewRecipe()
                    }
                } else {//User is in this fragment to edit an existing recipe
                    updateRecipe()
                }
            }

        }

        imgRecipe.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)

            startActivityForResult(intent, 1)
        }

        imgNewIngredient.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)

            startActivityForResult(intent, 2)
        }

    }

    private fun initIngredientRecyclerView() {
        rvIngredientList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ingredientListAdapter
        }
    }

    private fun initInstructionRecyclerView() {


        val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Specify the directions of movement
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Notify your adapter that an item is moved from x position to y position

                val removedInstruction: Instruction =
                    instructionListAdapter.getInstructionList().removeAt(viewHolder.adapterPosition)

                instructionListAdapter.getInstructionList()
                    .add(target.adapterPosition, removedInstruction)

                instructionListAdapter.notifyItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                return true
            }

            override fun isLongPressDragEnabled(): Boolean {
                // true: if you want to start dragging on long press
                // false: if you want to handle it yourself
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                // Handle action state changes
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // Called by the ItemTouchHelper when the user interaction with an element is over and it also completed its animation
                // This is a good place to send update to your backend about changes
                instructionListAdapter.notifyDataSetChanged()

            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

        rvInstructionList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = instructionListAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun initUnitSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            spNewIngredientUnit.adapter = adapter

        }
    }


    private fun saveNewRecipe() {
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Saved",
                UIMessageType.Toast()
            )
        )


        val recipeType = when (chipGroup.checkedChipId) {
            R.id.chip_drink_recipe_new_edit -> {
                "drink"
            }
            R.id.chip_food_recipe_new_edit -> {
                "food"
            }
            else -> null
        }

        //create new recipeWithIngredients item
        val newRecipeWithIngredients = RecipeWithIngredients(
            Recipe(
                etRecipeName.text.toString().trim(),
                mRecipeImageURIString,
                recipeType!!
            ),
            ingredientListAdapter.getIngredientList(),
            ingredientListAdapter.getAmountsList(),
            instructionListAdapter.getInstructionList()
        )

        lifecycleScope.launch(Dispatchers.Main) {
            //save item to database
            val savingJob = viewModel.saveRecipeWithIngredients(newRecipeWithIngredients)

            //block the current co-routine until the job is completed
            savingJob.join()

            //navigate to back to recipe list
            Navigation.findNavController(requireView())
                .navigate(R.id.action_recipeNewEdit_to_recipeList)
        }


    }

    private fun updateRecipe() {
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Updated",
                UIMessageType.Toast()
            )
        )

        val recipeType = when (chipGroup.checkedChipId) {
            R.id.chip_drink_recipe_new_edit -> {
                "drink"
            }
            R.id.chip_food_recipe_new_edit -> {
                "food"
            }
            else -> null
        }

        //create new recipeWithIngredients item with data from screen
        val updatedRecipeWithIngredients = RecipeWithIngredients(
            Recipe(
                etRecipeName.text.toString().trim(),
                mRecipeImageURIString,
                recipeType!!
            ),
            ingredientListAdapter.getIngredientList(),
            ingredientListAdapter.getAmountsList(),
            instructionListAdapter.getInstructionList()
        )


        lifecycleScope.launch(Dispatchers.Main) {
            //update item in database
            val updatingJob = viewModel.updateRecipeWithIngredients(updatedRecipeWithIngredients)

            //block the current co-routine until the job is completed
            updatingJob.join()

            //navigate to back to recipe list
            val action = RecipeNewEditDirections.actionRecipeNewEditToRecipeDetail(mRecipeToEditUID)
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun openMenuItemsBottomSheet() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)

            customView(R.layout.bottom_sheet_with_rv)
            val rvIngredients = this.view.findViewById<RecyclerView>(R.id.rv_bottom_with_rv)
            val ingredientSearch = this.view.findViewById<SearchView>(R.id.search_bottom_with_rv)

            rvIngredients.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = bottomSheetIngredientListAdapter

                Log.d("debug", bottomSheetIngredientListAdapter.getIngredientList().toString())

            }

            title(R.string.select_ingredients)
            positiveButton(R.string.close)

            ingredientSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    bottomSheetIngredientListAdapter.filter.filter(newText)
                    return false
                }

            })
        }

        bottomSheetIngredientListAdapter.updateIngredientsList(
            null,
            ingredientListAdapter.getIngredientList(),
            ingredientListAdapter.getAmountsList()
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    data?.let { intent ->
                        mRecipeImageURIString = intent.extras?.get("photoURI").toString()

                        imgRecipe.loadImage(
                            intent.extras?.get("photoURI") as Uri?,
                            getProgressDrawable(requireContext())
                        )
                    }
                }

                2 -> {
                    data?.let { intent ->
                        mIngredientImageURIString = intent.extras?.get("photoURI").toString()

                        imgNewIngredient.loadImage(
                            intent.extras?.get("photoURI") as Uri?,
                            getProgressDrawable(requireContext())
                        )
                    }
                }
            }
        }
    }

    private fun confirmBackNavigation() {
        val callback: AreYouSureCallBack = object :
            AreYouSureCallBack {
            override fun proceed() {
                if (mRecipeToEditUID == -1L) { //User is in this fragment to make a new recipe
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_recipeNewEdit_to_recipeList)
                } else {//User is in this fragment to edit an existing recipe
                    val action =
                        RecipeNewEditDirections.actionRecipeNewEditToRecipeDetail(mRecipeToEditUID)
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

            override fun cancel() {
                //Do nothing
            }
        }

        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Are you sure you want to go back? Any information that was not saved on this page will be forever lost",
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    override fun addIngredientWithAmount(ingredient: Ingredient, amount: String, unit: String) {
        when {
            amount.isBlank() -> {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "You must first add an amount before adding this ingredient",
                        UIMessageType.ErrorDialog()
                    )
                )
            }
            else -> {

                val newAmount = Amount(amount.toFloat(), unit)

                val ingredientToAdd = viewModel.fetchIngredientWithName(ingredient.name)


                //add to recyclerview
                if (ingredientToAdd != null) {
                    ingredientListAdapter.addIngredientItem(ingredientToAdd, newAmount)
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "You have added ${ingredientToAdd.name.capitalize(Locale.ROOT)}",
                            UIMessageType.Toast()
                        )
                    )
                } else {
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "The item you tried to save was not found",
                            UIMessageType.ErrorDialog()
                        )
                    )
                }
            }
        }
    }

    override fun removeIngredientWithAmount(ingredient: Ingredient) {
        ingredientListAdapter.removeIngredientWithAmount(ingredient)
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "You have removed ${ingredient.name.capitalize(Locale.ROOT)}",
                UIMessageType.Toast()
            )
        )
    }

    private fun subscribeObservers() {
        viewModel.recipeToUpdateLiveData.observe(
            viewLifecycleOwner, { recipeWithIngredients ->
                recipeWithIngredients?.let {
                    etRecipeName.setText(recipeWithIngredients.recipe.name.capitalize(Locale.ROOT))

                    if (recipeWithIngredients.recipe.type.toLowerCase(Locale.ROOT) == "drink") {
                        chipGroup.check(R.id.chip_drink_recipe_new_edit)
                    } else {
                        chipGroup.check(R.id.chip_food_recipe_new_edit)
                    }

                    recipeWithIngredients.recipe.image?.let {
                        imgRecipe.loadImage(
                            Uri.parse(it),
                            getProgressDrawable(requireContext())
                        )
                        mRecipeImageURIString = it
                    }

                    ingredientListAdapter.updateIngredientList(
                        recipeWithIngredients.ingredients,
                        recipeWithIngredients.amounts
                    )

                    bottomSheetIngredientListAdapter.updateIngredientsList(
                        null,
                        ingredientListAdapter.getIngredientList(),
                        ingredientListAdapter.getAmountsList()
                    )

                    instructionListAdapter.updateInstructionList(recipeWithIngredients.instructions.sortedBy { it.instructionNumber })
                }
            })

        viewModel.ingredientListLiveData.observe(viewLifecycleOwner, { ingredientList ->
            ingredientList?.let { nnIngredientList ->
                Log.d("debug", nnIngredientList.toString())
                bottomSheetIngredientListAdapter.updateIngredientsList(
                    nnIngredientList.sortedBy { it.name },
                    null,
                    null
                )
            }
        })
    }
}



