package com.barryalan.winetraining.ui.menu.menuItems

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
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
import com.barryalan.winetraining.model.menu.Instruction
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.ui.menu.recipes.RecipeInstructionsListAdapter
import com.barryalan.winetraining.ui.menu.recipes.RecipeListWithAmountsAdapter
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.CameraActivity
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.*
import java.util.*

class MenuItemNewEdit : BaseFragment(), BottomSheetRecipeCallback {

    private lateinit var imgMenuItem: ImageView
    private lateinit var etMenuItemName: EditText
    private lateinit var rvRecipeList: RecyclerView
    private lateinit var btnEditRecipes: MaterialButton
    private lateinit var btnUpdateSaveMenuItem: Button
    private lateinit var btnCancel: MaterialButton
    private lateinit var chipGroup: ChipGroup
    private lateinit var btnAddInstruction: MaterialButton
    private lateinit var etNewInstructionText: EditText
    private lateinit var rvInstructionList: RecyclerView

    private val instructionListAdapter = RecipeInstructionsListAdapter(arrayListOf())
    private val recipeListAdapter = RecipeListWithAmountsAdapter(arrayListOf())
    private lateinit var viewModel: MenuItemNewEditViewModel
    private val bottomSheetRecipeListAdapter = BottomSheetRecipeListAdapter(
        arrayListOf(),
        this,
        arrayListOf(),
        arrayListOf(),
    )

    private var mMenuItemToEditUID: Long = -1L
    private var mMenuItemImageURIString: String? = null

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
        return inflater.inflate(R.layout.fragment_menu_item_new_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgMenuItem = view.findViewById(R.id.img_menu_item_menu_item_new_edit)
        etMenuItemName = view.findViewById(R.id.et_menu_item_name_menu_item_new_edit)
        etNewInstructionText = view.findViewById(R.id.et_new_instruction_text_menu_item_new_edit)
        rvRecipeList = view.findViewById(R.id.rv_recipe_list_menu_item_new_edit)
        rvInstructionList = view.findViewById(R.id.rv_instruction_list_menu_item_new_edit)
        btnEditRecipes = view.findViewById(R.id.btn_edit_recipes_menu_item_new_edit)
        btnUpdateSaveMenuItem = view.findViewById(R.id.btn_update_save_menu_item_menu_item_new_edit)
        btnCancel = view.findViewById(R.id.btn_cancel_menu_item_new_edit)
        btnAddInstruction = view.findViewById(R.id.btn_add_instruction_menu_item_new_edit)
        chipGroup = view.findViewById(R.id.chip_group_menu_item_new_edit)

        viewModel = ViewModelProvider(this).get(MenuItemNewEditViewModel::class.java)
        viewModel.fetchRecipeList()
        arguments?.let {
            mMenuItemToEditUID = MenuItemNewEditArgs.fromBundle(it).menuItemUID
            viewModel.fetchSelectedMenuItemWithRecipes(mMenuItemToEditUID)
        }

        initRecipesRecyclerView()
        initInstructionRecyclerView()
        subscribeObservers()

        btnEditRecipes.setOnClickListener {
            openMenuItemsBottomSheet()
        }

        btnCancel.setOnClickListener {
            confirmBackNavigation()
        }

        btnUpdateSaveMenuItem.setOnClickListener {

            //Check to make sure there is a name on the recipe
            if (etMenuItemName.text.isEmpty()) {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "Menu Item must have a name",
                        UIMessageType.ErrorDialog()
                    )
                )
            } else if (chipGroup.checkedChipId == View.NO_ID) {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "Menu Item must have a type. (Drink or Food)",
                        UIMessageType.ErrorDialog()
                    )
                )
            } else {
                //User is in this fragment to make a new recipe
                if (mMenuItemToEditUID == -1L) {
                    if (etMenuItemName.text.toString() == "menu item") {
                        viewModel.initGloriasMenuItems()
                    } else if (etMenuItemName.text.toString() == "clear") {
                        viewModel.nukeMenuItems()
                    } else {
                        saveNewMenuItem()
                    }
                } else {//User is in this fragment to edit an existing recipe
                    updateMenuItem()
                }
            }

        }

        imgMenuItem.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)

            startActivityForResult(intent, 1)
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
    }

    private fun initRecipesRecyclerView() {
        rvRecipeList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recipeListAdapter
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

    private fun saveNewMenuItem() {
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Saved",
                UIMessageType.Toast()
            )
        )

        val menuItemType = when (chipGroup.checkedChipId) {
            R.id.chip_drink_menu_item_new_edit -> {
                "drink"
            }
            R.id.chip_food_menu_item_new_edit -> {
                "food"
            }
            else -> null
        }

        val newMenuItem = MenuItem(
            etMenuItemName.text.toString().trim(),
            mMenuItemImageURIString,
            menuItemType!!
        )


        lifecycleScope.launch(Dispatchers.Main) {
            //save item to database
            val savingJob = viewModel.saveMenuItemWithRecipes(
                newMenuItem,
                recipeListAdapter.getRecipeList(),
                recipeListAdapter.getAmountsList(),
                instructionListAdapter.getInstructionList()
            )

            //block the current co-routine until the job is completed
            savingJob.join()

            //navigate to back to menu item list
            Navigation.findNavController(requireView())
                .navigate(R.id.action_menuItemNewEdit_to_menuItemsList)
        }
    }

    private fun updateMenuItem() {
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Updated",
                UIMessageType.Toast()
            )
        )

        val menuItemType = when (chipGroup.checkedChipId) {
            R.id.chip_drink_menu_item_new_edit -> {
                "drink"
            }
            R.id.chip_food_menu_item_new_edit -> {
                "food"
            }
            else -> null
        }

        val updatedMenuItem = MenuItem(
            etMenuItemName.text.toString().trim(),
            mMenuItemImageURIString,
            menuItemType!!,
        )

        updatedMenuItem.id = mMenuItemToEditUID

        lifecycleScope.launch(Dispatchers.Main) {
            //update item in database
            val updatingJob = viewModel.updateMenuItemWithRecipes(
                updatedMenuItem,
                recipeListAdapter.getRecipeList(),
                recipeListAdapter.getAmountsList(),
                instructionListAdapter.getInstructionList()
            )

            //block the current co-routine until the job is completed
            updatingJob.join()

            //navigate to back to menu item list
            val action =
                MenuItemNewEditDirections.actionMenuItemNewEditToMenuItemDetail(mMenuItemToEditUID)
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun openMenuItemsBottomSheet() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)

            customView(R.layout.bottom_sheet_with_rv)
            val rvRecipes = this.view.findViewById<RecyclerView>(R.id.rv_bottom_with_rv)

            rvRecipes.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = bottomSheetRecipeListAdapter
            }

            title(R.string.select_recipes)
            positiveButton(R.string.close)
        }

        bottomSheetRecipeListAdapter.updateMenuItemsList(
            null,
            recipeListAdapter.getRecipeList(),
            recipeListAdapter.getAmountsList()
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    data?.let { intent ->
                        mMenuItemImageURIString = intent.extras?.get("photoURI").toString()

                        imgMenuItem.loadImage(
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

                //User is in this fragment to make a new menu item
                if (mMenuItemToEditUID == -1L) {

                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_menuItemNewEdit_to_menuItemsList)
                } else {//User is in this fragment to edit an existing menu item

                    val action =
                        MenuItemNewEditDirections.actionMenuItemNewEditToMenuItemDetail(
                            mMenuItemToEditUID
                        )
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

            override fun cancel() {}
        }

        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Are you sure you want to go back? Any information that was not saved on this page will be forever lost",
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    override fun addRecipeWithAmount(recipe: Recipe, amount: String, unit: String) {
        when {
            amount.isBlank() -> {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "You must first add an amount before adding this recipe",
                        UIMessageType.ErrorDialog()
                    )
                )
            }
            else -> {

                val newAmount = Amount(amount.toFloat(), unit)

                val recipeToAdd = viewModel.fetchRecipeWithName(recipe.name)


                //add to recyclerview
                if (recipeToAdd != null) {
                    recipeListAdapter.addRecipeItem(recipeToAdd, newAmount)
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "You have added ${recipeToAdd.name.capitalize(Locale.ROOT)}",
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

    override fun removeRecipeWithAmount(recipe: Recipe) {
        recipeListAdapter.removeRecipeWithAmount(recipe)
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "You have removed ${recipe.name.capitalize(Locale.ROOT)}",
                UIMessageType.Toast()
            )
        )
    }

    private fun subscribeObservers() {
        viewModel.menuItemToUpdateLiveData.observe(
            viewLifecycleOwner, { menuItemWithRecipes ->
                menuItemWithRecipes?.let {
                    etMenuItemName.setText(menuItemWithRecipes.menuItem.name.capitalize(Locale.ROOT))

                    if (menuItemWithRecipes.menuItem.type.decapitalize(Locale.ROOT) == "drink") {
                        chipGroup.check(R.id.chip_drink_menu_item_new_edit)
                    } else {
                        chipGroup.check(R.id.chip_food_menu_item_new_edit)
                    }

                    menuItemWithRecipes.menuItem.image?.let {
                        imgMenuItem.loadImage(
                            Uri.parse(it),
                            getProgressDrawable(requireContext())
                        )
                        mMenuItemImageURIString = it
                    }

                    recipeListAdapter.updateRecipeList(
                        menuItemWithRecipes.recipes,
                        menuItemWithRecipes.amounts
                    )
                    instructionListAdapter.updateInstructionList(
                        menuItemWithRecipes.instructions
                    )
                }
            })

        viewModel.recipesListLiveData.observe(viewLifecycleOwner, { recipeList ->
            recipeList?.let { nnRecipeList ->
                bottomSheetRecipeListAdapter.updateMenuItemsList(
                    nnRecipeList.sortedBy { it.name },
                    recipeListAdapter.getRecipeList(),
                    recipeListAdapter.getAmountsList()
                )
            }
        })
    }


}




